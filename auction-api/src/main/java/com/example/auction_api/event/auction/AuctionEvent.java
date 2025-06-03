package com.example.auction_api.event.auction;

import lombok.Getter;

@Getter
public abstract class AuctionEvent {

    private final String userEmail;
    private final String auctionTitle;

    protected AuctionEvent(String userEmail, String auctionTitle) {
        this.userEmail = userEmail;
        this.auctionTitle = auctionTitle;
    }
}
