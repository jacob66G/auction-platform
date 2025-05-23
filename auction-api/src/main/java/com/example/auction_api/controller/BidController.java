package com.example.auction_api.controller;

import com.example.auction_api.dto.request.BidRequest;
import com.example.auction_api.dto.response.BidResponse;
import com.example.auction_api.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getBidsForAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok().body(bidService.getBidByAuctionId(auctionId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BidResponse>> getBidsForUser() {
        return ResponseEntity.ok().body(bidService.getBidsByUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BidResponse> getBidById(@PathVariable Long id) {
        return ResponseEntity.ok().body(bidService.getBidById(id));
    }

    @PostMapping
    public ResponseEntity<BidResponse> createBid(@Valid @RequestBody BidRequest bid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bidService.createBid(bid));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long id) {
        bidService.deleteBid(id);
        return ResponseEntity.noContent().build();
    }
}
