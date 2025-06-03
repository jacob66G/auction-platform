package com.example.auction_api.controller;

import com.example.auction_api.dto.request.AuctionRequestComment;
import com.example.auction_api.dto.request.AuctionRequestCriteria;
import com.example.auction_api.dto.response.AuctionRequestResponse;
import com.example.auction_api.service.AuctionRequestService;
import com.example.auction_api.service.ModerationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class AuctionModerationController {

    private final ModerationService moderationService;
    private final AuctionRequestService auctionRequestService;

    public AuctionModerationController(ModerationService moderationService, AuctionRequestService auctionRequestService) {
        this.moderationService = moderationService;
        this.auctionRequestService = auctionRequestService;
    }

    @GetMapping()
    public ResponseEntity<List<AuctionRequestResponse>> getRequests(
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) LocalDateTime requestDate,
            @RequestParam(defaultValue = "requestDate") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        AuctionRequestCriteria criteria = AuctionRequestCriteria.builder()
                .types(types)
                .statuses(statuses)
                .requestDate(requestDate)
                .sortBy(sortBy)
                .ascending(ascending)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok().body(auctionRequestService.getAuctionRequestsByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionRequestResponse> getAuctionRequest(@PathVariable Long id) {
        return ResponseEntity.ok().body(auctionRequestService.getAuctionRequestById(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id, @Valid @RequestBody AuctionRequestComment decision) {
        moderationService.approveRequest(id, decision);
        return ResponseEntity.ok("Request approved successfully");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id, @Valid @RequestBody AuctionRequestComment decision) {
        moderationService.rejectRequest(id, decision);
        return ResponseEntity.ok("Request rejected successfully");
    }

}
