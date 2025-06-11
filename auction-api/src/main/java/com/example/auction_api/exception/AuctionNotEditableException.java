package com.example.auction_api.exception;


public class AuctionNotEditableException extends RuntimeException {
    public AuctionNotEditableException(Long id, String message) {
        super("Auction with id: " + id + " is not editable because " + message);
    }

    public AuctionNotEditableException(String message) {
        super(message);
    }
}
