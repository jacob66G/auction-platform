package com.example.auction_api.dao;

import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.entity.Auction;

import java.util.List;
import java.util.Optional;

public interface AuctionDao {
    List<Auction> findByCriteria(AuctionSearchCriteria criteria);
    List<Auction> findByUserIdAndCriteria(Long userId, AuctionSearchCriteria criteria);
    List<Auction> findByTitle(String title, Long id);
    List<Auction> findByCategoryId(Long categoryId);
    Optional<Auction> findById(Long id);
    Auction save(Auction auction);
    Auction update(Auction auction);
    void deleteById(Long id);
}
