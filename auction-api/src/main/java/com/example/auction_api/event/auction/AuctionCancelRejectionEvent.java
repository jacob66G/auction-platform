package com.example.auction_api.event.auction;

import lombok.Getter;

@Getter
public class AuctionCancelRejectionEvent extends AuctionEvent {

    private final String comment;

    public AuctionCancelRejectionEvent(String userEmail, String auctionTitle, String comment) {
        super(userEmail, auctionTitle);
        this.comment = comment;
    }
}
