package com.example.auction_api.dao;

import com.example.auction_api.dto.request.AuctionRequestCriteria;
import com.example.auction_api.entity.AuctionRequest;

import java.util.List;
import java.util.Optional;

public interface AuctionRequestDao {

    List<AuctionRequest> findByCriteria(AuctionRequestCriteria criteria);
    Optional<AuctionRequest> findById(Long id);
    AuctionRequest save(AuctionRequest auctionRequest);
    void delete(AuctionRequest auctionRequest);
}
