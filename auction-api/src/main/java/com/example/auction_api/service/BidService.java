package com.example.auction_api.service;

import com.example.auction_api.dto.request.BidRequest;
import com.example.auction_api.dto.response.BidResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Bid;

import java.util.List;

public interface BidService {
    List<BidResponse> getBidsByUser();
    List<BidResponse> getBidByAuctionId(Long auctionId);
    BidResponse getBidById(Long id);
    BidResponse createBid(BidRequest bidRequest);
    Bid getWinnerBid(Long auctionId);
}
