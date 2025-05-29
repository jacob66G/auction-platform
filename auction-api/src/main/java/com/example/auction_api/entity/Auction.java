package com.example.auction_api.entity;

import com.example.auction_api.dto.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "auction")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "starting_price")
    private BigDecimal startingPrice;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AuctionStatus auctionStatus;

    @Column(name = "actual_price")
    private BigDecimal actualPrice;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionImg> auctionImgs;

    @OneToMany(mappedBy = "auction", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval = true)
    @OrderBy("amount desc")
    private List<Bid> bids;

    public void addAuctionImg(AuctionImg auctionImg) {
        if (this.auctionImgs == null) {
            this.auctionImgs = new ArrayList<>();
        }

        this.auctionImgs.add(auctionImg);
        auctionImg.setAuction(this);
    }

    public void addBid(Bid bid) {
        if (this.bids == null) {
            this.bids = new ArrayList<>();
        }

        this.bids.add(bid);
        bid.setAuction(this);
    }

    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startingPrice=" + startingPrice +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", auctionStatus=" + auctionStatus +
                ", actualPrice=" + actualPrice +
                ", user=" + user.getUsername() +
                ", category=" + category.getName() +
                '}';
    }
}
