package com.example.auction_api.event;

import com.example.auction_api.entity.Auction;

public record AuctionCancellationRejectionEvent(Auction auction) {}
