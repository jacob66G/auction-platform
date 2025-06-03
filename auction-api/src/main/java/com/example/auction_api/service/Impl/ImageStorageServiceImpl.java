package com.example.auction_api.service.Impl;

import com.example.auction_api.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final S3Client s3;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    public ImageStorageServiceImpl(S3Client s3) {
        this.s3 = s3;
    }

    @Override
    public String uploadAuctionImage(Long auctionId, MultipartFile file) {
        try {
            String key = "auction_" + auctionId + "/" + file.getOriginalFilename();

            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));

            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    @Override
    public void deleteAuctionImage(String url) {
        String key = extractKeyFromUrl(url);

        s3.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf(".com/")  + 5);
    }
}
