package com.example.auction_api.exception;

public class AuctionNotCancellableException extends RuntimeException {
    public AuctionNotCancellableException(Long auctinId, String message) {
        super("Auction with id " + auctinId + " is not cancellable because of " + message);
    }
}
