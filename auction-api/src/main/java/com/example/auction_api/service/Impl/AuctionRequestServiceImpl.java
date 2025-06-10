package com.example.auction_api.service.Impl;

import com.example.auction_api.dao.AuctionRequestDao;
import com.example.auction_api.dto.request.AuctionRequestCriteria;
import com.example.auction_api.dto.response.AuctionRequestDetailsResponse;
import com.example.auction_api.dto.response.AuctionRequestResponse;
import com.example.auction_api.entity.Auction;
import com.example.auction_api.entity.AuctionRequest;
import com.example.auction_api.enums.RequestStatus;
import com.example.auction_api.enums.RequestType;
import com.example.auction_api.exception.ResourceNotFoundException;
import com.example.auction_api.mapper.AuctionRequestMapper;
import com.example.auction_api.service.AuctionRequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuctionRequestServiceImpl implements AuctionRequestService {

    private final AuctionRequestDao auctionRequestDao;
    private final AuctionRequestMapper mapper;

    public AuctionRequestServiceImpl(AuctionRequestDao auctionRequestDao, AuctionRequestMapper mapper) {
        this.auctionRequestDao = auctionRequestDao;
        this.mapper = mapper;
    }

    @Override
    public List<AuctionRequestResponse> getAuctionRequestsByCriteria(AuctionRequestCriteria criteria) {
        if (criteria.getSize() < 0) criteria.setSize(10);
        if (criteria.getPage() < 0) criteria.setPage(0);

        return auctionRequestDao.findByCriteria(criteria).stream().map(mapper::toResponse).toList();
    }

    @Override
    public AuctionRequestDetailsResponse getAuctionRequestById(Long id) {
        AuctionRequest request = auctionRequestDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AuctionRequest.class.getSimpleName(), id));

        return mapper.toDetailsResponse(request);
    }

    @Override
    public void createModerationRequest(Auction auction, RequestType requestType, String userReason) {

        AuctionRequest request = new AuctionRequest();
        request.setAuction(auction);
        request.setRequestType(requestType);
        request.setRequestStatus(RequestStatus.PENDING);
        request.setUserReason(userReason);
        request.setRequestDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        request.setRequestedBy(auction.getUser());

        auctionRequestDao.save(request);
    }

    @Override
    public long countByAuctionIdAndRequestType(Long id, RequestType requestType) {
        return auctionRequestDao.countByAuctionIdAndRequestType(id, requestType);
    }
}
