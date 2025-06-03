package com.example.auction_api.service;


import java.math.BigDecimal;

public interface EmailSenderService {
    void sendAuctionWonToWinner(String userEmail, String auctionTitle, String winnerName, BigDecimal winnerBidAmount);
    void sendAuctionEndedToOwner(String userEmail, String auctionTitle, String winnerName, BigDecimal winnerBidAmount);
    void sendAuctionExpiredToOwner(String userEmail, String auctionTitle);
    void sendAuctionApprovalToOwner(String userEmail, String auctionTitle);
    void sendAuctionRejectToOwner(String userEmail, String auctionTitle, String comment);
    void sendAuctionCancelApprovalToOwner(String userEmail, String auctionTitle, String comment);
    void sendAuctionCancelRejectionToOwner(String userEmail, String auctionTitle, String comment);
    void sendUserRegistrationInfo(String userEmail, String username);
    void sendRefundInfoToBidder(String userEmail, String auctionTitle, BigDecimal amount);
}
