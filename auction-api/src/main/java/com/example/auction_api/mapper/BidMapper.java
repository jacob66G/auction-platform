package com.example.auction_api.mapper;

import com.example.auction_api.dto.response.BidResponse;
import com.example.auction_api.entity.Bid;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public BidResponse toResponse(Bid bid) {
        return new BidResponse(
                bid.getId(),
                bid.getAmount(),
                bid.getBitDate(),
                bid.getUser().getUsername()
        );
    }

}
