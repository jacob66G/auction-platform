package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.AuctionDao;
import com.example.auction_api.dto.request.AuctionRequest;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.dto.response.AuctionDetailsResponse;
import com.example.auction_api.dto.response.AuctionResponse;
import com.example.auction_api.dto.response.MessageResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.dto.enums.AuctionStatus;
import com.example.auction_api.entity.Bid;
import com.example.auction_api.entity.Category;
import com.example.auction_api.entity.User;
import com.example.auction_api.event.*;
import com.example.auction_api.exception.*;
import com.example.auction_api.mapper.AuctionMapper;
import com.example.auction_api.service.*;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionDao auctionDao;
    private final CategoryService categoryService;
    private final AuctionMapper mapper;
    private final AuthenticationServiceImpl authService;
    private final UserService userService;
    private final BidService bidService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AuctionServiceImpl(
            AuctionDao auctionDao,
            CategoryService categoryService,
            AuctionMapper mapper,
            AuthenticationServiceImpl authService,
            UserService userService, BidService bidService,
            AuctionEventListener auctionEventListener,
            ApplicationEventPublisher applicationEventPublisher) {
        this.auctionDao = auctionDao;
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.authService = authService;
        this.userService = userService;
        this.bidService = bidService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public List<AuctionResponse> getActiveAuctionsByCriteria(AuctionSearchCriteria criteria) {
        return auctionDao.findByCriteria(criteria).stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<AuctionResponse> getAuctionsByCriteria(AuctionSearchCriteria criteria) {
        return auctionDao.findByCriteria(criteria).stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<AuctionResponse> getAuctionsByUserAndCriteria(AuctionSearchCriteria criteria) {
        User user = authService.getAuthenticatedUser();
        return auctionDao.findByUserIdAndCriteria(user.getId(), criteria).stream().map(mapper::toResponse).toList();
    }

    @Override
    public AuctionDetailsResponse getAuctionById(Long id) {
        Auction auction = auctionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Auction.class.getSimpleName(), id));

        return mapper.toDetailsResponse(auction);
    }

    @Override
    @Transactional
    public AuctionResponse createAuction(AuctionRequest auction) {
        User user = authService.getAuthenticatedUser();

        validateTitle(auction.title(), null);
        validateStartTimeAndEndTime(auction.startTime(), auction.endTime());
        Category category = categoryService.getCategoryEntityById(auction.categoryId());

        Auction newAuction = mapper.toEntity(auction);
        newAuction.setCategory(category);
        newAuction.setAuctionStatus(AuctionStatus.PENDING_APPROVAL);
        newAuction.setActualPrice(auction.startingPrice());

        user.addAuction(newAuction);

        //load images to s3

        return mapper.toResponse(auctionDao.save(newAuction));
    }

    @Override
    @Transactional
    public AuctionResponse updateAuction(Long id, AuctionRequest auction) {
        User user = authService.getAuthenticatedUser();

        Auction existingAuction = getAuctionEntityById(id);
        validateOwner(existingAuction, user);

        if(!existingAuction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            throw new AuctionNotEditableException(id, "is not active");
        }

        if(!existingAuction.getBids().isEmpty()) {
            throw new AuctionNotEditableException(id, "has already been bidded");
        }

        validateStartTimeAndEndTime(auction.startTime(), auction.endTime());
        validateTitle(auction.title(), id);
        Category category = categoryService.getCategoryEntityById(auction.categoryId());

        existingAuction.setTitle(auction.title());
        existingAuction.setDescription(auction.description());
        existingAuction.setCategory(category);
        existingAuction.setStartingPrice(auction.startingPrice());
        existingAuction.setEndTime(auction.endTime().truncatedTo(ChronoUnit.MINUTES));
        existingAuction.setAuctionStatus(AuctionStatus.PENDING_APPROVAL);

        return mapper.toResponse(auctionDao.update(existingAuction));
    }

    @Override
    @Transactional
    public void deleteAuction(Long id) {
        getAuctionEntityById(id);
        auctionDao.deleteById(id);
    }

    @Override
    @Transactional
    public MessageResponse cancelAuction(Long auctionId) {
        User user = authService.getAuthenticatedUser();
        Auction auction = getAuctionEntityById(auctionId);

        validateOwner(auction, user);

        if(!auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            throw new AuctionNotCancellableException(auctionId, "auction is not active");
        }

        if(auction.getBids().isEmpty()) {
            auction.setAuctionStatus(AuctionStatus.CANCELLED);
            auctionDao.update(auction);

            return new MessageResponse("Auction cancellation successfully approved.");

        } else {
            auction.setAuctionStatus(AuctionStatus.PENDING_CANCELLED);
            auctionDao.update(auction);

            return new MessageResponse("Auction cancellation has been forwarded for approval.");
        }
    }

    @Override
    public Auction getAuctionEntityById(Long id) {
        return auctionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Auction.class.getSimpleName(), id));
    }

    @Override
    public List<Auction> getAuctionsByCategory(Long categoryId) {
        return auctionDao.findByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public void approveDeletionAuction(Long auctionId) {
        Auction auction = getAuctionEntityById(auctionId);

        if(!auction.getAuctionStatus().equals(AuctionStatus.PENDING_CANCELLED)) {
            throw new InvalidAuctionStatusException(auctionId, auction.getAuctionStatus(), AuctionStatus.PENDING_CANCELLED);
        }

        refundBidders(auction);

        auction.setAuctionStatus(AuctionStatus.CANCELLED);
        auctionDao.update(auction);

        applicationEventPublisher.publishEvent(new AuctionCancellationApprovalEvent(auction));
    }

    @Override
    @Transactional
    public void rejectDeletionAuction(Long auctionId) {
        Auction auction = getAuctionEntityById(auctionId);

        if(!auction.getAuctionStatus().equals(AuctionStatus.PENDING_CANCELLED)) {
            throw new InvalidAuctionStatusException(auctionId, auction.getAuctionStatus(), AuctionStatus.PENDING_CANCELLED);
        }

        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auctionDao.update(auction);

        applicationEventPublisher.publishEvent(new AuctionCancellationRejectionEvent(auction));
    }

    @Override
    @Transactional
    public void approveSaveAuction(Long auctionId) {
        Auction auction = getAuctionEntityById(auctionId);

        if(!auction.getAuctionStatus().equals(AuctionStatus.PENDING_APPROVAL)) {
            throw new InvalidAuctionStatusException(auctionId, auction.getAuctionStatus(), AuctionStatus.PENDING_APPROVAL);
        }

        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auctionDao.update(auction);

        applicationEventPublisher.publishEvent(new AuctionApprovalEvent(auction));
    }

    @Override
    @Transactional
    public void rejectSaveAuction(Long auctionId) {
        Auction auction = getAuctionEntityById(auctionId);

        if(!auction.getAuctionStatus().equals(AuctionStatus.PENDING_APPROVAL)) {
            throw new InvalidAuctionStatusException(auctionId, auction.getAuctionStatus(), AuctionStatus.PENDING_APPROVAL);
        }

        auction.setAuctionStatus(AuctionStatus.REJECTED);
        auctionDao.update(auction);

        applicationEventPublisher.publishEvent(new AuctionRejectEvent(auction));
    }

    @Override
    @Transactional
    public void endOfAuction(Auction auction) {
        if(auction.getEndTime().isBefore(LocalDateTime.now()) && auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            if(auction.getBids().isEmpty()) {
                auction.setAuctionStatus(AuctionStatus.EXPIRED);
            } else {
                auction.setAuctionStatus(AuctionStatus.FINISHED);

                Bid winnerBid = bidService.getWinnerBid(auction.getId());
                applicationEventPublisher.publishEvent(new AuctionEndEvent(auction, winnerBid));
            }
            auctionDao.update(auction);
        }
    }

    private void validateTitle(String title, Long auctionId) {
        if(!auctionDao.findByTitle(title, auctionId).isEmpty()) {
            throw new DuplicateNameException(Auction.class.getSimpleName(), title);
        }
    }

    private void validateStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
        endTime = endTime.truncatedTo(ChronoUnit.MINUTES);


        if(startTime.isBefore(now)) {
            throw new InvalidAuctionDurationException("Start time must be in the future.");
        }
        else if (endTime.isBefore(startTime.plusDays(1))) {
            throw new InvalidAuctionDurationException("Auction must last at least 1 full day.");
        }
    }

    private void validateOwner(Auction existingAuction, User user) {
        if(!Objects.equals(existingAuction.getUser().getId(), user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this auction.");
        }
    }

    private void refundBidders(Auction auction) {
        if(!auction.getBids().isEmpty()) {
            for(Bid bid : auction.getBids()) {
                User user = bid.getUser();
                BigDecimal amount = bid.getAmount();

                user.setBalance(user.getBalance().add(amount));
                userService.refund(bid.getAmount(), user);

                bidService.deleteBid(bid.getId());
            }
        }
    }
}
