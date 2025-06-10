package com.example.auction_api.controller;

import com.example.auction_api.dto.request.AuctionCancelRequest;
import com.example.auction_api.dto.request.AuctionCreateDto;
import com.example.auction_api.dto.response.AuctionCreateResponse;
import com.example.auction_api.enums.AuctionStatus;
import com.example.auction_api.dto.request.AuctionSearchCriteria;
import com.example.auction_api.dto.response.AuctionDetailsResponse;
import com.example.auction_api.dto.response.AuctionResponse;
import com.example.auction_api.dto.response.MessageResponse;
import com.example.auction_api.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuctionResponse>> publicSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        AuctionSearchCriteria criteria = AuctionSearchCriteria.builder()
                .title(title)
                .username(username)
                .categoryIds(categoryIds)
                .statuses(List.of(AuctionStatus.ACTIVE.name()))
                .sortBy(sortBy)
                .ascending(ascending)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok().body(auctionService.getActiveAuctionsByCriteria(criteria));
    }

    @GetMapping("/search/my")
    public ResponseEntity<List<AuctionResponse>> ownSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        AuctionSearchCriteria criteria = AuctionSearchCriteria.builder()
                .title(title)
                .categoryIds(categoryIds)
                .statuses(statuses)
                .sortBy(sortBy)
                .ascending(ascending)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok().body(auctionService.getAuctionsByUserAndCriteria(criteria));
    }

    @GetMapping("/search/admin")
    public ResponseEntity<List<AuctionResponse>> privateSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        AuctionSearchCriteria criteria = AuctionSearchCriteria.builder()
                .title(title)
                .username(username)
                .categoryIds(categoryIds)
                .statuses(statuses)
                .sortBy(sortBy)
                .ascending(ascending)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok().body(auctionService.getAuctionsByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionDetailsResponse> getAuctionById(@PathVariable Long id) {
        return ResponseEntity.ok().body(auctionService.getAuctionById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuctionCreateResponse> createAuction(
            @Valid @RequestPart("auction") AuctionCreateDto auction,
            @RequestPart(value= "images", required = false) MultipartFile[] images
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.createAuction(auction, images));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAuction(@PathVariable Long id) {
        auctionService.deleteAuction(id);
        return ResponseEntity.ok().body(new MessageResponse("Auction successfully deleted."));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<MessageResponse> cancelAuction(@PathVariable Long id, @Valid @RequestBody AuctionCancelRequest request) {
        return ResponseEntity.ok().body(auctionService.cancelAuction(id, request));
    }
}
