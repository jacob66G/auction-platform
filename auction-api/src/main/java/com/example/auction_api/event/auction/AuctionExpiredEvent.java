package com.example.auction_api.event.auction;


public class AuctionExpiredEvent extends AuctionEvent {

    public AuctionExpiredEvent(String userEmail, String auctionTitle) {
        super(userEmail, auctionTitle);
    }
}
