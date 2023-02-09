package com.test.fedex.trackstatus.model;

import com.test.fedex.common.enums.TrackStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains track status of an order.
 */
@Builder
@Getter
public class OrderTrackStatus {

    private String orderNumber;
    private TrackStatus status;
}
