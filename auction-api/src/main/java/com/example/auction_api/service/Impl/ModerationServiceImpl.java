package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.AuctionDao;
import com.example.auction_api.dao.AuctionRequestDao;
import com.example.auction_api.dto.request.AuctionRequestComment;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.AuctionRequest;
import com.example.auction_api.entity.User;
import com.example.auction_api.enums.AuctionStatus;
import com.example.auction_api.enums.RequestStatus;
import com.example.auction_api.event.auction.AuctionApproveEvent;
import com.example.auction_api.event.auction.AuctionCancelApprovalEvent;
import com.example.auction_api.event.auction.AuctionCancelRejectionEvent;
import com.example.auction_api.event.auction.AuctionRejectEvent;
import com.example.auction_api.exception.InvalidAuctionRequestStatusException;
import com.example.auction_api.exception.ResourceNotFoundException;
import com.example.auction_api.service.AuthenticationService;
import com.example.auction_api.service.BidService;
import com.example.auction_api.service.ModerationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class ModerationServiceImpl implements ModerationService {

    private final AuthenticationService authService;
    private final BidService bidService;
    private final AuctionDao auctionDao;
    private final AuctionRequestDao auctionRequestDao;
    private final ApplicationEventPublisher eventPublisher;

    public ModerationServiceImpl(
            AuthenticationService authService,
            BidService bidService,
            AuctionDao auctionDao,
            AuctionRequestDao auctionRequestDao,
            ApplicationEventPublisher eventPublisher
    ) {
        this.authService = authService;
        this.bidService = bidService;
        this.auctionDao = auctionDao;
        this.auctionRequestDao = auctionRequestDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void approveRequest(Long id, AuctionRequestComment decision) {
        User moderator = authService.getAuthenticatedUser();
        AuctionRequest request = getPendingRequest(id);

        switch (request.getRequestType()) {
            case SAVE -> {
                approveCreateRequest(request);
                updateRequestStatus(request, RequestStatus.APPROVE, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionApproveEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle()));
            }
            case EDIT -> {
                approveEditRequest(request);
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

        switch (request.getRequestType()) {
            case SAVE -> {
                rejectCreateRequest(request);
                updateRequestStatus(request, RequestStatus.REJECT, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionRejectEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle(), decision.comment()));
            }
            case EDIT -> {
                updateRequestStatus(request, RequestStatus.REJECT, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionRejectEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle(), decision.comment()));
            }

            case CANCEL -> {
                updateRequestStatus(request, RequestStatus.REJECT, decision.comment(), moderator);
                eventPublisher.publishEvent(new AuctionCancelRejectionEvent(request.getAuction().getUser().getEmail(), request.getAuction().getTitle(), decision.comment()));
            }
        }
    }

    private AuctionRequest getPendingRequest(Long id) {
        AuctionRequest request = auctionRequestDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AuctionRequest.class.getSimpleName(), id));

        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new InvalidAuctionRequestStatusException(id, request.getRequestStatus(), RequestStatus.PENDING);
        }

        return request;
    }

    private void approveCreateRequest(AuctionRequest request) {
        Auction auction = request.getAuction();
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auctionDao.update(auction);
    }

    private void approveEditRequest(AuctionRequest request) {
        Auction auction = request.getAuction();
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auctionDao.update(auction);
    }

    private void approveCancelRequest(AuctionRequest request) {
        Auction auction = request.getAuction();
        auction.setAuctionStatus(AuctionStatus.CANCELLED);

        bidService.refundBidders(auction);

        auctionDao.update(auction);
    }

    private void rejectCreateRequest(AuctionRequest request) {
        Auction auction = request.getAuction();
        auction.setAuctionStatus(AuctionStatus.REJECTED);

        auctionDao.update(auction);
    }

    private void updateRequestStatus(AuctionRequest request, RequestStatus status, String comment, User moderator) {
        request.setRequestStatus(status);
        request.setModeratorComment(comment);
        request.setDecisionDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        request.setModeratedBy(moderator);

        auctionRequestDao.save(request);
    }

}
