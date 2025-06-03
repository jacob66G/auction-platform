package com.example.auction_api.exception;

import com.example.auction_api.enums.RequestStatus;

public class InvalidAuctionRequestStatusException extends RuntimeException {
    public InvalidAuctionRequestStatusException(Long auctionRequestId, RequestStatus currentStatus, RequestStatus expectedStatus) {
        super("Auction request with id " + auctionRequestId + " has invalid status: " + currentStatus + ". Expected: " + expectedStatus);
    }
}
