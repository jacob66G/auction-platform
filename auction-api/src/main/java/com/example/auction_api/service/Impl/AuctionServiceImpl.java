package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.AuctionDao;
import com.example.auction_api.dto.request.AuctionCancelRequest;
import com.example.auction_api.dto.request.AuctionCreateDto;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.dto.response.AuctionCreateResponse;
import com.example.auction_api.dto.response.AuctionDetailsResponse;
import com.example.auction_api.dto.response.AuctionResponse;
import com.example.auction_api.dto.response.MessageResponse;
import com.example.auction_api.entity.*;
import com.example.auction_api.enums.AuctionStatus;
import com.example.auction_api.enums.RequestType;
import com.example.auction_api.event.auction.AuctionEndEvent;
import com.example.auction_api.event.auction.AuctionExpiredEvent;
import com.example.auction_api.exception.*;
import com.example.auction_api.mapper.AuctionMapper;
import com.example.auction_api.service.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionDao auctionDao;
    private final CategoryService categoryService;
    private final AuctionRequestService auctionRequestService;
    private final AuctionMapper mapper;
    private final AuthenticationServiceImpl authService;
    private final BidService bidService;
    private final ImageStorageService imgStorageService;
    private final ApplicationEventPublisher eventPublisher;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png"
    );

    public AuctionServiceImpl(
            AuctionDao auctionDao,
            CategoryService categoryService,
            AuctionRequestService auctionRequestService,
            AuctionMapper mapper,
            AuthenticationServiceImpl authService,
            BidService bidService,
            ImageStorageService imgStorageService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.auctionDao = auctionDao;
        this.categoryService = categoryService;
        this.auctionRequestService = auctionRequestService;
        this.mapper = mapper;
        this.authService = authService;;
        this.bidService = bidService;
        this.imgStorageService = imgStorageService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<AuctionResponse> getActiveAuctionsByCriteria(AuctionSearchCriteria criteria) {
        if(criteria.getSize() < 0) criteria.setSize(10);
        if(criteria.getPage() < 0) criteria.setPage(0);

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
    public AuctionCreateResponse createAuction(AuctionCreateDto auction, MultipartFile[] images) {
        User user = authService.getAuthenticatedUser();

        validateImages(images);
        validateTitle(auction.title(), null);
        validateStartTimeAndEndTime(auction.startTime(), auction.endTime());
        Category category = categoryService.getCategoryEntityById(auction.categoryId());

        Auction newAuction = mapper.toEntity(auction);
        newAuction.setCategory(category);
        newAuction.setAuctionStatus(AuctionStatus.PENDING_APPROVAL);
        newAuction.setActualPrice(auction.startingPrice());
        user.addAuction(newAuction);

        Auction savedAuction = auctionDao.save(newAuction);

        attachImagesToAuction(images, savedAuction);

        auctionRequestService.createModerationRequest(savedAuction, RequestType.SAVE, null);

        String message = "Your auction has been submitted for approval.\nYou will be notified by email once it's reviewed.";
        return mapper.toCreateResponse(savedAuction, message);
    }


//    @Override
//    @Transactional
//    public AuctionResponse updateAuction(Long id, AuctionCreateDto auction, MultipartFile[] images) {
//        User user = authService.getAuthenticatedUser();
//
//        Auction existingAuction = getAuctionEntityById(id);
//
//        validateOwner(existingAuction, user);
//        validateAuctionCanBeEdited(existingAuction);
//        validateImages(images);
//        validateStartTimeAndEndTime(auction.startTime(), auction.endTime());
//        validateTitle(auction.title(), id);
//        Category category = categoryService.getCategoryEntityById(auction.categoryId());
//
//        existingAuction.setTitle(auction.title());
//        existingAuction.setDescription(auction.description());
//        existingAuction.setCategory(category);
//        existingAuction.setStartingPrice(auction.startingPrice());
//        existingAuction.setEndTime(auction.endTime().truncatedTo(ChronoUnit.MINUTES));
//
//        attachImagesToAuction(images, existingAuction);
//
//        auctionRequestService.createModerationRequest(existingAuction, RequestType.EDIT, null);
//
//        return mapper.toResponse(auctionDao.update(existingAuction));
//    }


    @Override
    @Transactional
    public MessageResponse cancelAuction(Long auctionId, AuctionCancelRequest cancelRequest) {
        User user = authService.getAuthenticatedUser();
        Auction auction = getAuctionEntityById(auctionId);

        validateOwner(auction, user);
        validateAuctionCanBeCancelled(auction);

        if(auction.getBids().isEmpty()) {
            auction.setAuctionStatus(AuctionStatus.CANCELLED);
            auctionDao.update(auction);

            return new MessageResponse("Your auction cancellation request has been successfully approved.");

        } else {
            auctionRequestService.createModerationRequest(auction, RequestType.CANCEL, cancelRequest.reason());
            return new MessageResponse("Your request to cancel the auction has been submitted for approval.\n" +
                    "You will be notified by email once it's reviewed.");
        }
    }


    @Override
    @Transactional
    public void deleteAuction(Long id) {
        Auction auction = getAuctionEntityById(id);

        for (AuctionImg img : auction.getAuctionImgs()) {
            imgStorageService.deleteAuctionImage(img.getUrl());
        }

        auctionDao.deleteById(id);
    }


    @Override
    @Transactional
    public void endOfAuction(Auction auction) {
        if(auction.getEndTime().isBefore(LocalDateTime.now()) && auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            if(auction.getBids().isEmpty()) {
                auction.setAuctionStatus(AuctionStatus.EXPIRED);
                eventPublisher.publishEvent(new AuctionExpiredEvent(auction.getUser().getEmail(), auction.getTitle()));

            } else {
                auction.setAuctionStatus(AuctionStatus.FINISHED);

                Bid winnerBid = bidService.getWinnerBid(auction.getId());
                eventPublisher.publishEvent(new AuctionEndEvent(
                        auction.getUser().getEmail(),
                        auction.getTitle(),
                        winnerBid.getUser().getUsername(),
                        winnerBid.getAmount(),
                        winnerBid.getUser().getEmail()
                    )
                );
            }
            auctionDao.update(auction);
        }
    }

    private void attachImagesToAuction(MultipartFile[] images, Auction auction) {
        if(images != null) {
            for(MultipartFile image : images) {
                String url = imgStorageService.uploadAuctionImage(auction.getId(), image);
                AuctionImg img = new AuctionImg();
                img.setUrl(url);

                auction.addAuctionImg(img);
            }
        }
    }

    private void validateAuctionCanBeEdited(Auction auction) {
        if(!auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            throw new AuctionNotEditableException(auction.getId(), "is not active");
        }

        if(!auction.getBids().isEmpty()) {
            throw new AuctionNotEditableException(auction.getId(), "has already been bidded");
        }
    }

    private void validateImages(MultipartFile[] images) {
        if(images != null) {
            if(images.length > 5) {
                throw new ValidationImagesException("Only 5 images upload is allowed");
            }

            for(MultipartFile file : images) {
                if(!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                    throw new ValidationImagesException("Not allowed content type! Only jpg/png is allowed");
                }
            }
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

    private void validateAuctionCanBeCancelled(Auction auction) {
        if(!auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            throw new AuctionNotEditableException(auction.getId(), "auction is not active");
        }
    }
}
