package com.example.auction_api.mapper;

import com.example.auction_api.dto.response.AuctionRequestResponse;
import com.example.auction_api.entity.AuctionRequest;
import org.springframework.stereotype.Component;

@Component
public class AuctionRequestMapper {

    public AuctionRequestResponse toResponse(AuctionRequest request) {
        return new AuctionRequestResponse(
                request.getId(),
                request.getAuction().getTitle(),
                request.getRequestType().name(),
                request.getRequestStatus().name(),
                request.getUserReason(),
                request.getModeratorComment(),
                request.getRequestDate(),
                request.getDecisionDate(),
                request.getRequestedBy().getUsername(),
                request.getModeratedBy() != null ? request.getModeratedBy().getUsername() : null
        );
    }
}
