package com.example.auction_api.mapper;

import com.example.auction_api.dto.request.AuctionCreateDto;
import com.example.auction_api.dto.response.AuctionCreateResponse;
import com.example.auction_api.dto.response.AuctionDetailsResponse;
import com.example.auction_api.dto.response.AuctionResponse;
import com.example.auction_api.dto.response.BidResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.AuctionImg;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuctionMapper {

    private final BidMapper bidMapper;

    public AuctionMapper(BidMapper bidMapper) {
        this.bidMapper = bidMapper;
    }

    public AuctionResponse toResponse(Auction auction) {
        String auctionImgUrl = auction.getAuctionImgs() != null && !auction.getAuctionImgs().isEmpty()
                ? auction.getAuctionImgs().getFirst().getUrl()
                : null;

        return new AuctionResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getEndTime(),
                auction.getUser().getUsername(),
                auction.getCategory().getName(),
                auctionImgUrl,
                auction.getActualPrice()
        );
    }

    public AuctionDetailsResponse toDetailsResponse(Auction auction) {
        List<String> auctionImgsUrls = auction.getAuctionImgs() != null
                ? auction.getAuctionImgs().stream()
                .map(AuctionImg::getUrl)
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<BidResponse> bidResponses = auction.getBids().stream().map(bidMapper::toResponse).toList();

        return new AuctionDetailsResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getStartingPrice().doubleValue(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getAuctionStatus().name(),
                auction.getUser().getUsername(),
                auction.getCategory().getName(),
                auctionImgsUrls,
                auction.getActualPrice(),
                bidResponses
        );
    }

    public AuctionCreateResponse toCreateResponse(Auction auction, String message) {
        List<String> auctionImgsUrls = auction.getAuctionImgs() != null
                ? auction.getAuctionImgs().stream()
                .map(AuctionImg::getUrl)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new AuctionCreateResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getStartingPrice().doubleValue(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getAuctionStatus().name(),
                auction.getUser().getUsername(),
                auction.getCategory().getName(),
                auctionImgsUrls,
                message
        );
    }

    public Auction toEntity(AuctionCreateDto request) {
        Auction auction = new Auction();
        auction.setTitle(request.title());
        auction.setDescription(request.description());
        auction.setStartingPrice(request.startingPrice());
        auction.setStartTime(request.startTime().truncatedTo(ChronoUnit.MINUTES));
        auction.setEndTime(request.endTime().truncatedTo(ChronoUnit.MINUTES));

        return auction;
    }

}
