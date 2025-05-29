package com.example.auction_api.entity;

import com.example.auction_api.dto.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    private BigDecimal balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auction> auctions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids;

    public void addAuction(Auction auction) {
        if (this.auctions == null) {
            this.auctions = new ArrayList<>();
        }

        this.auctions.add(auction);
        auction.setUser(this);
    }

    public void addBid(Bid bid) {
        if (this.bids == null) {
            this.bids = new ArrayList<>();
        }
        this.bids.add(bid);
        bid.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role.name() +
                ", balance=" + balance +
                '}';
    }
}
