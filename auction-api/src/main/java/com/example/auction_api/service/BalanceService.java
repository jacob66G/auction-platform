package com.example.auction_api.service;

import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.User;

import java.math.BigDecimal;

public interface BalanceService {
    void blockFunds(User user, Auction auction, BigDecimal amount);
    void releaseFunds(User user, Auction auction);
    void unlockFunds(User user, Auction auction);
    void finalizeFunds(User user, Auction auction);
    BigDecimal getAvailableFunds(User user);
}
