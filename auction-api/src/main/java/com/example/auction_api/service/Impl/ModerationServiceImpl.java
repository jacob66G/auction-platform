package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.AuctionDao;
import com.example.auction_api.dao.AuctionRequestDao;
import com.example.auction_api.dto.request.AuctionRequestComment;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.AuctionRequest;
import com.example.auction_api.entity.Bid;
import com.example.auction_api.entity.User;
import com.example.auction_api.enums.AuctionStatus;
import com.example.auction_api.enums.RequestStatus;
import com.example.auction_api.event.auction.AuctionApproveEvent;
import com.example.auction_api.event.auction.AuctionCancelApprovalEvent;
import com.example.auction_api.event.auction.AuctionCancelRejectionEvent;
import com.example.auction_api.event.auction.AuctionRejectEvent;
import com.example.auction_api.exception.ForbiddenException;
import com.example.auction_api.exception.InvalidAuctionRequestStatusException;
import com.example.auction_api.exception.ResourceNotFoundException;
import com.example.auction_api.service.AuthenticationService;
import com.example.auction_api.service.BalanceService;
import com.example.auction_api.service.BidService;
import com.example.auction_api.service.ModerationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModerationServiceImpl implements ModerationService {

    private final AuthenticationService authService;
    private final BidService bidService;
    private final BalanceService balanceService;
    private final AuctionDao auctionDao;
    private final AuctionRequestDao auctionRequestDao;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageStorageServiceImpl imgStorageService;

    public ModerationServiceImpl(
            AuthenticationService authService,
            BidService bidService, BalanceService balanceService,
            AuctionDao auctionDao,
            AuctionRequestDao auctionRequestDao,
            ApplicationEventPublisher eventPublisher, ImageStorageServiceImpl imgStorageService
    ) {
        this.authService = authService;
        this.bidService = bidService;
        this.balanceService = balanceService;
        this.auctionDao = auctionDao;
        this.auctionRequestDao = auctionRequestDao;
        this.eventPublisher = eventPublisher;
        this.imgStorageService = imgStorageService;
    }

    @Override
    public void approveRequest(Long id, AuctionRequestComment decision) {
        User moderator = authService.getAuthenticatedUser();
        AuctionRequest request = getPendingRequest(id);

        validateModeratorIsNotConflicted(moderator, request.getAuction());

        switch (request.getRequestType()) {
            case SAVE -> {
                approveCreateRequest(request);
                updateRequestStatus(request, RequestStatus.APPROVE, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionApproveEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle()));
            }

            case CANCEL -> {
                approveCancelRequest(request);
                updateRequestStatus(request, RequestStatus.APPROVE, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionCancelApprovalEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle(), decision.comment()));
            }
        }
    }

    @Override
    public void rejectRequest(Long id, AuctionRequestComment decision) {
        User moderator = authService.getAuthenticatedUser();
        AuctionRequest request = getPendingRequest(id);

        validateModeratorIsNotConflicted(moderator, request.getAuction());

        switch (request.getRequestType()) {
            case SAVE -> {
                String userEmail = request.getAuction().getUser().getEmail();
                String auctionTitle = request.getAuction().getTitle();

                rejectCreateRequest(request);
                eventPublisher.publishEvent(new AuctionRejectEvent(userEmail, auctionTitle, decision.comment()));
            }

            case CANCEL -> {
                rejectCancelRequest(request);
                updateRequestStatus(request, RequestStatus.REJECT, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionCancelRejectionEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle(), decision.comment()));
            }
        }
    }

    private void approveCreateRequest(AuctionRequest request) {
        Auction auction = request.getAuction();

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(auction.getDuration().getDays());

        auction.setStartTime(startTime);
        auction.setEndTime(endTime);

        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auctionDao.update(auction);
    }


    private void approveCancelRequest(AuctionRequest request) {
        Auction auction = request.getAuction();
        auction.setAuctionStatus(AuctionStatus.CANCELLED);

        Bid highestBid = bidService.getWinnerBid(auction.getId());
        balanceService.unlockFunds(highestBid.getUser(), auction);

        auction.getBids().clear();

        auctionDao.update(auction);
    }

    private void rejectCreateRequest(AuctionRequest request) {
        Auction auction = request.getAuction();

        auction.getAuctionImgs()
                .forEach(img -> imgStorageService.deleteAuctionImage(img.getUrl()));
        
        auctionRequestDao.delete(request);
        auctionDao.delete(auction);
    }

    private void rejectCancelRequest(AuctionRequest request) {
        Auction auction = request.getAuction();
        auction.setCancelRequestPending(false);

        auctionDao.update(auction);
    }

    private void updateRequestStatus(AuctionRequest request, RequestStatus status, String comment, User moderator) {
        request.setRequestStatus(status);
        request.setModeratorComment(comment);
        request.setDecisionDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        request.setModeratedBy(moderator);

        auctionRequestDao.save(request);
    }

    private AuctionRequest getPendingRequest(Long id) {
        AuctionRequest request = auctionRequestDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AuctionRequest.class.getSimpleName(), id));

        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new InvalidAuctionRequestStatusException(id, request.getRequestStatus(), RequestStatus.PENDING);
        }

        return request;
    }

    private void validateModeratorIsNotConflicted(User user, Auction auction) {
        Long userId = user.getId();

        if(auction.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to moderate your own auctions.");
        }

        boolean userHasBid = auction.getBids().stream()
                .anyMatch(bid -> bid.getUser().getId().equals(userId));


        if(userHasBid) {
            throw new ForbiddenException("You are not allowed to moderate your auctions you bidded.");
        }

    }

}
