package com.test.fedex.trackstatus.service;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.common.enums.TrackStatus;
import com.test.fedex.track.generated.api.TrackServiceApi;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderTrackStatusServiceTest {

    private ApiConfigProperties apiConfigProperties = mock(ApiConfigProperties.class);
    private TrackServiceApi trackServiceApi = mock(TrackServiceApi.class);

    private TrackStatusService trackStatusService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(apiConfigProperties, trackServiceApi);
        trackStatusService = new TrackStatusService(apiConfigProperties, trackServiceApi);
    }

    @Test
    public void testTrackingStatus_whenNullInput() {
        assertEquals(Collections.emptyMap(), trackStatusService.trackingStatus(null));
    }

    @Test
    public void testTrackingStatus_whenTrackStatusApiThrowsException() {

        //GIVEN
        val orderNumbers = List.of(123456);

        //WHEN
        when(trackServiceApi.getTrackingStatus(123456)).thenThrow(HttpServerErrorException.class);
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);

        val result = trackStatusService.trackingStatus(orderNumbers);

        //ASSERT
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    public void testTrackingStatus() {

        //GIVEN
        val orderNumbers = List.of(123456);

        //WHEN
        when(trackServiceApi.getTrackingStatus(123456)).thenReturn("DELIVERED");
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);

        val result = trackStatusService.trackingStatus(orderNumbers);

        //ASSERT
        assertAll(
                () -> assertTrue(result.containsKey("123456")),
                () -> assertEquals(TrackStatus.DELIVERED, result.get("123456"))
        );
    }
}
