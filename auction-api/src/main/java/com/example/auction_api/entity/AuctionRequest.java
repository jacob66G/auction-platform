package com.example.auction_api.entity;

import com.example.auction_api.enums.RequestStatus;
import com.example.auction_api.enums.RequestType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class AuctionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Column(name = "reason")
    private String userReason;

    @Column(name = "moderator_comment")
    private String moderatorComment;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderatedBy;
}
