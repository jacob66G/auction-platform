package com.example.auction_api.dao;

import com.example.auction_api.dao.impl.AuctionDaoImpl;
import com.example.auction_api.dao.impl.BidDaoImpl;
import com.example.auction_api.dao.impl.UserDaoImpl;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.Bid;
import com.example.auction_api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BidDaoImpl.class, UserDaoImpl.class, AuctionDaoImpl.class})
@ActiveProfiles("test")
public class BidDaoTest {

    @Autowired
    private BidDao bidDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuctionDao auctionDao;

    @Test
    void findByAuctionId_ShouldReturnBidsForGivenAuction() {
        // given
        Long auctionId = 1L;

        // when
        List<Bid> result = bidDao.findByAuctionId(auctionId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(bid -> bid.getAuction().getId().equals(auctionId));
    }

    @Test
    void findByUserId_ShouldReturnBidsForGivenUser() {
        // given
        Long userId = 1L;

        // when
        List<Bid> result = bidDao.findByUserId(userId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(bid -> bid.getUser().getId().equals(userId));
    }

    @Test
    void findById_ShouldReturnCorrectBid() {
        // given
        Long bidId = 1L;

        // when
        Optional<Bid> result = bidDao.findById(bidId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(bidId);
    }

    @Test
    void save_ShouldPersistBid() {
        // given
        User user = userDao.findById(1L).get();
        Auction auction = auctionDao.findAuctionById(1L).get();

        Bid bid = new Bid();
        bid.setAmount(BigDecimal.valueOf(200));
        bid.setBitDate(LocalDateTime.now());
        bid.setUser(user);
        bid.setAuction(auction);

        // when
        Bid savedBid = bidDao.save(bid);

        // then
        assertThat(savedBid.getId()).isNotNull();
    }

    @Test
    void update_ShouldModifyBidAmount() {
        // given
        Bid bid = bidDao.findById(1L).orElseThrow();
        BigDecimal newAmount = BigDecimal.valueOf(999);

        // when
        bid.setAmount(newAmount);
        Bid updatedBid = bidDao.update(bid);

        // then
        assertThat(updatedBid.getAmount()).isEqualTo(newAmount);
    }

    @Test
    void deleteById_ShouldRemoveBid() {
        // given
        Long bidId = 1L;
        assertThat(bidDao.findById(bidId)).isPresent();

        // when
        bidDao.deleteById(bidId);

        // then
        assertThat(bidDao.findById(bidId)).isNotPresent();
    }
}