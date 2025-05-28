package com.example.auction_api.scheduler;

import com.example.auction_api.dao.AuctionDao;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.service.AuctionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AuctionSchedulerService {

    private final AuctionService auctionService;
    private final AuctionDao auctionDao;

    public AuctionSchedulerService(AuctionService auctionService, AuctionDao auctionDao) {
        this.auctionService = auctionService;
        this.auctionDao = auctionDao;
    }

    @Scheduled(fixedRate = 60000)
    public void endExpiredAuctions() {
        List<Auction> auctions = auctionDao.findByEndTimeBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        for(Auction auction: auctions) {
            auctionService.endOfAuction(auction);
        }
    }
}
