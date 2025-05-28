package com.example.auction_api.service;

import com.example.auction_api.dto.request.AuctionRequest;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.dto.response.AuctionDetailsResponse;
import com.example.auction_api.dto.response.AuctionResponse;
import com.example.auction_api.dto.response.MessageResponse;
import com.example.auction_api.entity.Auction;

import java.util.List;

public interface AuctionService {
    List<AuctionResponse> getActiveAuctionsByCriteria(AuctionSearchCriteria criteria);
    List<AuctionResponse> getAuctionsByCriteria(AuctionSearchCriteria criteria);
    List<AuctionResponse> getAuctionsByUserAndCriteria(AuctionSearchCriteria criteria);
    AuctionDetailsResponse getAuctionById(Long id);
    Auction getAuctionEntityById(Long id);
    List<Auction> getAuctionsByCategory(Long categoryId);
    AuctionResponse createAuction(AuctionRequest auction);
    AuctionResponse updateAuction(Long id, AuctionRequest auction);
    void deleteAuction(Long id);
    MessageResponse cancelAuction(Long id);
    void approveDeletionAuction(Long id);
    void rejectDeletionAuction(Long id);
    void approveSaveAuction(Long id);
    void rejectSaveAuction(Long id);
    void endOfAuction(Auction auction);
}
