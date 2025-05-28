package com.example.auction_api.dao;

import com.example.auction_api.entity.Bid;

import java.util.List;
import java.util.Optional;

public interface BidDao {
    List<Bid> findByAuctionId(Long auctionId);
    List<Bid> findByUserId(Long userId);
    Optional<Bid> findById(Long id);
    Bid save(Bid bid);
    Bid update(Bid bid);
    void deleteById(Long id);
    Optional<Bid> findTop1ByAuctionIdOrderByAmountDesc(Long auctionId);
}
