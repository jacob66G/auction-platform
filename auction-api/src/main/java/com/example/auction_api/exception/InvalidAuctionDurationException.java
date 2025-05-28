package com.example.auction_api.exception;

public class InvalidAuctionDurationException extends RuntimeException {
    public InvalidAuctionDurationException(String message) {
        super(message);
    }
}
