package com.test.fedex.trackstatus.model;

import com.test.fedex.common.enums.TrackStatus;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTrackStatusTest {

    @Test
    public void testGetters() {
        //GIVEN
        val trackStatus = OrderTrackStatus.builder()
                .orderNumber("123456")
                .status(TrackStatus.DELIVERED)
                .build();

        //ASSERT
        assertAll(
                () -> assertEquals("123456", trackStatus.getOrderNumber()),
                () -> assertEquals(TrackStatus.DELIVERED, trackStatus.getStatus())
        );
    }
}
