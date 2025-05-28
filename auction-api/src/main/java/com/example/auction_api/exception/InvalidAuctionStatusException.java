package com.example.auction_api.exception;

import com.example.auction_api.dto.enums.AuctionStatus;

public class InvalidAuctionStatusException extends RuntimeException {
    public InvalidAuctionStatusException(Long auctionId, AuctionStatus currentStatus, AuctionStatus expectedStatus) {
        super("Auction with ID " + auctionId + " has invalid status: " + currentStatus + ". Expected: " + expectedStatus);
    }
}
