package com.example.auction_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "balance_lock")
public class BalanceLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    private BigDecimal amount;

    public BalanceLock(User user, Auction auction, BigDecimal amount) {
        this.user = user;
        this.auction = auction;
        this.amount = amount;
    }
}
