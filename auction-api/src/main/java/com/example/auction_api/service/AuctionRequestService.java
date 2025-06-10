package com.example.auction_api.service;

import com.example.auction_api.dto.request.AuctionRequestCriteria;
import com.example.auction_api.dto.response.AuctionRequestDetailsResponse;
import com.example.auction_api.dto.response.AuctionRequestResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.enums.RequestType;

import java.util.List;

public interface AuctionRequestService {
    List<AuctionRequestResponse> getAuctionRequestsByCriteria(AuctionRequestCriteria criteria);
    AuctionRequestDetailsResponse getAuctionRequestById(Long id);
    void createModerationRequest(Auction auction, RequestType requestType, String userReason);
    long countByAuctionIdAndRequestType(Long id, RequestType requestType);
}
