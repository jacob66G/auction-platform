package com.example.auction_api.event.auction;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AuctionEndEvent extends AuctionEvent{

    private final String winnerName;
    private final BigDecimal winnerBidAmount;
    private final String winnerEmail;

    public AuctionEndEvent(String userEmail, String auctionTitle, String winnerName, BigDecimal winnerBidAmount, String winnerEmail) {
        super(userEmail, auctionTitle);
        this.winnerName = winnerName;
        this.winnerBidAmount = winnerBidAmount;
        this.winnerEmail = winnerEmail;
    }
}
