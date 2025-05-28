package com.example.auction_api.exception;

public class InvalidBidderException extends RuntimeException {
    public InvalidBidderException(String message) {
        super(message);
    }
}
