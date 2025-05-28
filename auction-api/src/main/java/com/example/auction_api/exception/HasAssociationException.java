package com.example.auction_api.exception;

public class HasAssociationException extends RuntimeException {
    public HasAssociationException(String resource, Long id) {
        super("You can not delete " + resource + " with id " + id + " because it has associated auctions");
    }
}
