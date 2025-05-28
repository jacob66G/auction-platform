package com.example.auction_api.service;

import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Bid;
import com.example.auction_api.entity.User;

public interface EmailSenderService {
    void sendAuctionWonToWinner(Auction auction, Bid winnerBid);
    void sendAuctionEndedToOwner(Auction auction, Bid winnerBid);
    void sendAuctionExpiredToOwner(Auction auction);
    void sendAuctionApprovalToOwner(Auction auction);
    void sendAuctionRejectionToOwner(Auction auction);
    void sendAuctionCancellationApprovalToOwner(Auction auction);
    void sendAuctionCancellationRejectionToOwner(Auction auction);
    void sendAuctionCancellationToBidders(Auction auction);
    void sendUserRegistrationInfo(User user);
}
