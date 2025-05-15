package com.example.auction_api.dao;

import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.entity.Auction;

import java.util.List;
import java.util.Optional;

public interface AuctionDao {
    List<Auction> findAll();
    List<Auction> findByCriteria(AuctionSearchCriteria criteria); //find auctions by criteria
    List<Auction> findByUserId(Long userId, AuctionSearchCriteria criteria); //find auctions with bids by criteria
    List<Auction> findByTitle(String title, Long id);
    Optional<Auction> findAuctionById(Long id);
    Auction save(Auction auction);
    Auction update(Auction auction);
    void deleteById(Long id);
}
