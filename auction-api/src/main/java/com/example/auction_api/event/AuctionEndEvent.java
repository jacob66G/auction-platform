package com.example.auction_api.event;

import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Bid;

public record AuctionEndEvent(Auction auction, Bid winnerBid) {}
