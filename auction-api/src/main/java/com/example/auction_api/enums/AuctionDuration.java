package com.example.auction_api.enums;

import lombok.Getter;

@Getter
public enum AuctionDuration {
    ONE_DAY(1, "1 day"),
    THREE_DAYS(3, "3 days"),
    SEVEN_DAYS(7, "week"),
    TEN_DAYS(10, "10 days");

    private final int days;
    private final String label;

    AuctionDuration(int days, String label) {
        this.days = days;
        this.label = label;
    }

    public static AuctionDuration fromLabel(String label) {
        for(AuctionDuration duration : values()) {
            if(duration.label.equalsIgnoreCase(label)) {
                return duration;
            }
        }
        throw new IllegalArgumentException("Invalid auction duration: " + label);
    }
}
