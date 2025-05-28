package com.example.auction_api.exception;

public class DuplicateNameException extends RuntimeException {
    public DuplicateNameException(String resource, String name) {
        super(resource + " with name: " + name + " already exists");
    }
}
