package com.test.fedex.aggregation.service;

import com.test.fedex.aggregation.generated.model.AggregatedResult;
import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.common.enums.TrackStatus;
import com.test.fedex.pricing.service.PricingService;
import com.test.fedex.shipment.service.ShipmentService;
import com.test.fedex.trackstatus.service.TrackStatusService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AggregationServiceTest {

    private ApiConfigProperties apiConfigProperties = mock(ApiConfigProperties.class);
    private ShipmentService shipmentService = mock(ShipmentService.class);
    private TrackStatusService trackStatusService = mock(TrackStatusService.class);
    private PricingService pricingService = mock(PricingService.class);

    private AggregationService aggregationService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(apiConfigProperties, shipmentService, trackStatusService, pricingService);
        aggregationService = new AggregationService(apiConfigProperties, shipmentService, trackStatusService, pricingService);
    }

    @Test
    public void testAggregatedResult_whenNullInput() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val expectedAggregatedResult = AggregatedResult.builder()
                .shipments(Map.of())
                .track(Map.of())
                .pricing(Map.of())
                .build();

        val result = aggregationService.aggregatedResult(null, null, null);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedAggregatedResult, result)
        );
    }

    @Test
    public void testAggregatedResult() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val orders = List.of(12345);
        val countryCodes = List.of("US");
        val expectedAggregatedResult = AggregatedResult.builder()
                .shipments(Map.of("12345", List.of("BOX", "ENVELOPE")))
                .track(Map.of("12345", AggregatedResult.InnerEnum.DELIVERED))
                .pricing(Map.of("US", BigDecimal.ONE))
                .build();

        //WHEN
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);
        when(shipmentService.shipmentProducts(orders)).thenReturn(Map.of("12345", List.of("BOX", "ENVELOPE")));
        when(trackStatusService.trackingStatus(orders)).thenReturn(Map.of("12345", TrackStatus.DELIVERED));
        when(pricingService.countryBasePricing(countryCodes)).thenReturn(Map.of("US", BigDecimal.ONE));

        val result = aggregationService.aggregatedResult(orders, orders, countryCodes);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedAggregatedResult, result)
        );
    }
}
