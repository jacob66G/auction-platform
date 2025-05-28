package com.example.auction_api.exception;

public class InsufficientAmountException extends RuntimeException {
    public InsufficientAmountException() {
        super("You don't have enough amount to make this bid");
    }
}
