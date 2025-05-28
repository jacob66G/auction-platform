package com.example.auction_api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super("Resource " + resource + " with id: " + id + " not found");
    }

    public ResourceNotFoundException(String resource, String name) {
        super("Resource " + resource + " with name: " + name + " not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
