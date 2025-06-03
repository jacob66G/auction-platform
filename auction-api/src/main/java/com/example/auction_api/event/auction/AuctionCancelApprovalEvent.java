package com.example.auction_api.event.auction;

import lombok.Getter;

@Getter
public class AuctionCancelApprovalEvent extends AuctionEvent {

    private final String comment;

    public AuctionCancelApprovalEvent(String userEmail, String auctionTitle, String comment) {
        super(userEmail, auctionTitle);
        this.comment = comment;
    }
}
