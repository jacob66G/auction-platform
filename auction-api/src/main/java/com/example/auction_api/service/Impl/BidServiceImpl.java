package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.BidDao;
import com.example.auction_api.dto.request.BidRequest;
import com.example.auction_api.dto.response.BidResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.enums.AuctionStatus;
import com.example.auction_api.entity.Bid;
import com.example.auction_api.entity.User;
import com.example.auction_api.exception.*;
import com.example.auction_api.mapper.BidMapper;
import com.example.auction_api.service.AuctionService;
import com.example.auction_api.service.BidService;
import com.example.auction_api.service.UserService;
import org.hibernate.event.service.spi.EventActionWithParameter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BidServiceImpl implements BidService {

    private final BidDao bidDao;
    private final BidMapper mapper;
    private final AuctionService auctionService;
    private final AuthenticationServiceImpl authService;
    private final UserService userService;

    public BidServiceImpl(
            BidDao bidDao,
            BidMapper mapper,
            @Lazy AuctionService auctionService,
            AuthenticationServiceImpl authService,
            UserService userService
    ) {
        this.bidDao = bidDao;
        this.mapper = mapper;
        this.auctionService = auctionService;
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    public List<BidResponse> getBidsByUser() {
        User user = authService.getAuthenticatedUser();

        return bidDao.findByUserId(user.getId()).stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<BidResponse> getBidByAuctionId(Long id) {
        return bidDao.findByAuctionId(id).stream().map(mapper::toResponse).toList();
    }

    @Override
    public BidResponse getBidById(Long id) {
        Bid bid = bidDao.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(Bid.class.getSimpleName(), id));

        return mapper.toResponse(bid);
    }

    @Override
    @Transactional
    public BidResponse createBid(BidRequest bid) {
        User user = authService.getAuthenticatedUser();

        Auction auction = auctionService.getAuctionEntityById(bid.auctionId());

        validateAuctionStatus(auction);
        validateBidderIsNotOwner(auction, user);
        validateHighestBid(bid.amount(), bid.auctionId());

        userService.decreaseBalance(bid.amount(), user);

        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setBitDate(LocalDateTime.now());
        newBid.setAmount(bid.amount());

        user.addBid(newBid);
        auction.addBid(newBid);

        auction.setActualPrice(bid.amount());

        return mapper.toResponse(bidDao.save(newBid));
    }

    @Override
    @Transactional
    public void deleteBid(Long id) {
        bidDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Bid.class.getSimpleName(), id));

        bidDao.deleteById(id);
    }

    @Override
    public void refundBidders(Auction auction) {
        if(!auction.getBids().isEmpty()) {
            Map<User, BigDecimal> refundMap = new HashMap<>();

            for(Bid bid : auction.getBids()) {
                refundMap.merge(
                        bid.getUser(),
                        bid.getAmount(),
                        BigDecimal::add
                );
            }

            for (Bid bid : auction.getBids()) {
                bidDao.deleteById(bid.getId());
            }

            refundMap.forEach((user, amount) -> userService.refund(user, amount, auction));
        }
    }

    @Override
    public Bid getWinnerBid(Long auctionId) {
        return bidDao.findTop1ByAuctionIdOrderByAmountDesc(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("No bids for auction"));
    }

    private void validateHighestBid(BigDecimal amount, Long auctionId) {
          List<Bid> bids = bidDao.findByAuctionId(auctionId);

        if (!bids.stream().allMatch(b -> amount.compareTo(b.getAmount()) > 0)) {
            throw new BidTooLowException(amount);
        }
    }

    private void validateAuctionStatus(Auction auction) {
        if (!auction.getAuctionStatus().name().equals(AuctionStatus.ACTIVE.name())) {
            throw new ActionNotActiveException();
        }
    }

    private void validateBidderIsNotOwner(Auction auction, User user) {
        if (auction.getUser().getId().equals(user.getId())) {
            throw new InvalidBidderException("Owner cannot place bids on own auction");
        }
    }
}
