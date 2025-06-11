package com.example.auction_api.exception;

public class EmailChangeNotAllowedException extends RuntimeException {
    public EmailChangeNotAllowedException(String message) {
        super(message);
    }
}
