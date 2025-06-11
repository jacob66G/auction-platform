package com.example.auction_api.exception;

public class ActionNotActiveException extends RuntimeException {
    public ActionNotActiveException() {
        super("Auction is not active.");
    }
}
