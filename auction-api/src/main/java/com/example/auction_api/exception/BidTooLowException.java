package com.example.auction_api.exception;

import java.math.BigDecimal;

public class BidTooLowException extends RuntimeException {
    public BidTooLowException(BigDecimal amount) {
        super("Your bid: " + amount + " is too low");
    }
}
