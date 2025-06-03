package com.example.auction_api.service;

import com.example.auction_api.dto.request.AuctionCancelRequest;
import com.example.auction_api.dto.request.AuctionCreateDto;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.dto.response.AuctionCreateResponse;
import com.example.auction_api.dto.response.AuctionDetailsResponse;
import com.example.auction_api.dto.response.AuctionResponse;
import com.example.auction_api.dto.response.MessageResponse;
import com.example.auction_api.entity.Auction;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuctionService {
    List<AuctionResponse> getActiveAuctionsByCriteria(AuctionSearchCriteria criteria);
    List<AuctionResponse> getAuctionsByCriteria(AuctionSearchCriteria criteria);
    List<AuctionResponse> getAuctionsByUserAndCriteria(AuctionSearchCriteria criteria);
    AuctionDetailsResponse getAuctionById(Long id);
    Auction getAuctionEntityById(Long id);
    List<Auction> getAuctionsByCategory(Long categoryId);
    AuctionCreateResponse createAuction(AuctionCreateDto auction, MultipartFile[] images);
//    AuctionResponse updateAuction(Long id, AuctionCreateDto auction, MultipartFile[] images);
    void deleteAuction(Long id);
    MessageResponse cancelAuction(Long auctionId, AuctionCancelRequest cancelRequest);
    void endOfAuction(Auction auction);
}
