package com.example.auction_api.event.auction;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AuctionRefundEvent extends AuctionEvent {

    private final BigDecimal amount;

    public AuctionRefundEvent(String userEmail, String auctionTitle, BigDecimal amount) {
        super(userEmail, auctionTitle);
        this.amount = amount;
    }
}
