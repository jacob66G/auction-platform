package com.example.auction_api.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuctionSearchCriteria {
    private String title;
    private String username;
    private List<Integer> categoryIds;
    private List<String> statuses;
    private String sortBy;
    private boolean ascending;
    private int page;
    private int size;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AuctionSearchCriteria criteria = new AuctionSearchCriteria();

        public Builder title(String title) {
            criteria.title = title;
            return this;
        }

        public Builder username(String username) {
            criteria.username = username;
            return this;
        }

        public Builder categoryIds(List<Integer> ids) {
            criteria.categoryIds = ids;
            return this;
        }

        public Builder statuses(List<String> statuses) {
            criteria.statuses = statuses;
            return this;
        }

        public Builder sortBy(String field) {
            criteria.sortBy = field;
            return this;
        }

        public Builder ascending(boolean ascending) {
            criteria.ascending = ascending;
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

        public AuctionSearchCriteria build() {
            return criteria;
        }
    }
}
