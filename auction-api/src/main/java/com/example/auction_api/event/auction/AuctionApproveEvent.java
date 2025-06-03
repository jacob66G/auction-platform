package com.example.auction_api.event.auction;



public class AuctionApproveEvent extends AuctionEvent {

    public AuctionApproveEvent(String userEmail, String auctionTitle) {
        super(userEmail, auctionTitle);
    }
}
