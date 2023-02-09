package com.test.fedex.common.enums;

import java.util.Arrays;

public enum TrackStatus {

    UNKNOWN,

    NEW,
    IN_TRANSIT,
    COLLECTING,
    COLLECTED,
    DELIVERING,
    DELIVERED,
    ;

    public static TrackStatus getStatus(final String value) {
        if(value == null) {
            return TrackStatus.UNKNOWN;
        }

        return Arrays.stream(values())
                .filter(v -> v.name().equals(value))
                .findFirst()
                .orElse(TrackStatus.UNKNOWN);
    }
}
