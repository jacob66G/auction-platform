package com.example.auction_api.dto.request;


import java.util.List;

public record AuctionSearchCriteria(
        String title,
        String username,
        List<Integer> categoryIds,
        List<String> statuses,
        String sortBy,
        boolean ascending,
        int page,
        int size
) {
}
