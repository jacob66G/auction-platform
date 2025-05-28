package com.example.auction_api.event;

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
        senderService.sendAuctionExpiredToOwner(event.auction());
    }

    @EventListener
    public void handleAuctionEndEvent(AuctionEndEvent event) {
        senderService.sendAuctionEndedToOwner(event.auction(), event.winnerBid());
        senderService.sendAuctionWonToWinner(event.auction(), event.winnerBid());
    }

    @EventListener
    public void handleAuctionApprovalEvent(AuctionApprovalEvent event) {
        senderService.sendAuctionApprovalToOwner(event.auction());
    }

    @EventListener
    public void handleAuctionRejectEvent(AuctionRejectEvent event) {
        senderService.sendAuctionRejectionToOwner(event.auction());
    }

    @EventListener
    public void handleAuctionCancellationApprovalEvent(AuctionCancellationApprovalEvent event) {
        senderService.sendAuctionCancellationApprovalToOwner(event.auction());
        senderService.sendAuctionCancellationToBidders(event.auction());
    }

    @EventListener
    public void handleAuctionCancellationRejectionEvent(AuctionCancellationRejectionEvent event) {
        senderService.sendAuctionCancellationRejectionToOwner(event.auction());
    }
}
