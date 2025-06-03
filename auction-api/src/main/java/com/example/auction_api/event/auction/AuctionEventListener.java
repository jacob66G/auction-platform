package com.example.auction_api.event.auction;

import com.example.auction_api.service.EmailSenderService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuctionEventListener {

    private final EmailSenderService senderService;

    public AuctionEventListener(EmailSenderService senderService) {
        this.senderService = senderService;
    }

    @EventListener
    public void handleAuctionExpiredEvent(AuctionExpiredEvent event) {
        senderService.sendAuctionExpiredToOwner(event.getUserEmail(), event.getAuctionTitle());
    }

    @EventListener
    public void handleAuctionEndEvent(AuctionEndEvent event) {
        senderService.sendAuctionEndedToOwner(event.getUserEmail(), event.getAuctionTitle(), event.getWinnerName(), event.getWinnerBidAmount());
        senderService.sendAuctionWonToWinner(event.getWinnerEmail(), event.getAuctionTitle(), event.getWinnerName(), event.getWinnerBidAmount());
    }

    @EventListener
    public void handleAuctionApprovalEvent(AuctionApproveEvent event) {
        senderService.sendAuctionApprovalToOwner(event.getUserEmail(), event.getAuctionTitle());
    }

    @EventListener
    public void handleAuctionRejectEvent(AuctionRejectEvent event) {
        senderService.sendAuctionRejectToOwner(event.getUserEmail(), event.getAuctionTitle(), event.getComment());
    }

    @EventListener
    public void handleAuctionCancellationApprovalEvent(AuctionCancelApprovalEvent event) {
        senderService.sendAuctionCancelApprovalToOwner(event.getUserEmail(), event.getAuctionTitle(), event.getComment());
    }

    @EventListener
    public void handleAuctionCancellationRejectionEvent(AuctionCancelRejectionEvent event) {
        senderService.sendAuctionCancelRejectionToOwner(event.getUserEmail(), event.getAuctionTitle(), event.getComment());
    }

    @EventListener
    public void handleAuctionRefundEvent(AuctionRefundEvent event) {
        senderService.sendRefundInfoToBidder(event.getUserEmail(), event.getAuctionTitle(), event.getAmount());
    }
}
