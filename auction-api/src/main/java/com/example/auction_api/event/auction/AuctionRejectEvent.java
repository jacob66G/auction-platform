package com.example.auction_api.event.auction;

import lombok.Getter;

@Getter
public class AuctionRejectEvent extends AuctionEvent {

    private final String comment;

    public AuctionRejectEvent(String userEmail, String auctionTitle, String comment) {
        super(userEmail, auctionTitle);
        this.comment = comment;
    }
}
