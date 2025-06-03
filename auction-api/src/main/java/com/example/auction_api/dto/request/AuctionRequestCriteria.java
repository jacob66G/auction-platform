package com.example.auction_api.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AuctionRequestCriteria {

    private List<String> types;
    private List<String> statuses;
    private LocalDateTime requestDate;
    private String sortBy;
    private boolean ascending;
    private int page;
    private int size;

    public static Builder builder() {return new Builder(); }

    public static class Builder {
        private final AuctionRequestCriteria criteria = new AuctionRequestCriteria();

        public Builder types(List<String> types) {
            criteria.types = types;
            return this;
        }

        public Builder statuses(List<String> statuses) {
            criteria.statuses = statuses;
            return this;
        }

        public Builder requestDate(LocalDateTime requestDate) {
            criteria.requestDate = requestDate;
            return this;
        }

        public Builder sortBy(String sortBy) {
            criteria.sortBy = sortBy;
            return this;
        }

        public Builder ascending(boolean asc) {
            criteria.ascending = asc;
            return this;
        }

        public Builder page(int page) {
            criteria.page = page;
            return this;
        }

        public Builder size(int size) {
            criteria.size = size;
            return this;
        }

        public AuctionRequestCriteria build() { return criteria; }
    }
}
