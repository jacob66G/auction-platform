package com.example.auction_api.service;

import com.example.auction_api.dto.request.AuctionRequestComment;

public interface ModerationService {
    void approveRequest(Long id, AuctionRequestComment decision);
    void rejectRequest(Long id, AuctionRequestComment decision);
}
