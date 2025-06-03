package com.example.auction_api.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String uploadAuctionImage(Long auctionId, MultipartFile file);
    void deleteAuctionImage(String url);
}
