package com.test.fedex.aggregation.api;

import com.test.fedex.aggregation.generated.model.AggregatedResult;
import com.test.fedex.aggregation.service.AggregationService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

public class AggregationEndpointTest {

    private AggregationService aggregationService = mock(AggregationService.class);
    private AggregationEndpoint aggregationEndpoint;

    @BeforeEach
    public void setUp() {
        Mockito.reset(aggregationService);
        aggregationEndpoint = new AggregationEndpoint(aggregationService);
    }

    @Test
    public void testAggregatedResult_whenNullInput() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val expectedAggregatedResult = AggregatedResult.builder().build();
        val expectedResponse = ResponseEntity.ok(expectedAggregatedResult);

        //WHEN
        when(aggregationService.aggregatedResult(null, null, null)).thenReturn(expectedAggregatedResult);

        val result = aggregationEndpoint.aggregatedResult(null, null, null);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
        );
    }

    @Test
    public void testAggregatedResult_whenTimeout() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val expectedResponse = ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();

        //WHEN
        when(aggregationService.aggregatedResult(null, null, null))
                .thenThrow(TimeoutException.class);

        val result = aggregationEndpoint.aggregatedResult(null, null, null);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
        );
    }

    @Test
    public void testAggregatedResult_whenUnknownException() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val expectedResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        //WHEN
        when(aggregationService.aggregatedResult(null, null, null))
                .thenThrow(RuntimeException.class);

        val result = aggregationEndpoint.aggregatedResult(null, null, null);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
        );
    }

    @Test
    public void testAggregatedResult_whenNonNullShipmentOrders() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val orders = List.of(12345);
        val expectedAggregatedResult = AggregatedResult.builder()
                .shipments(Map.of("12345", List.of("BOX", "ENVELOPE")))
                .build();

        val expectedResponse = ResponseEntity.ok(expectedAggregatedResult);

        //WHEN
        when(aggregationService.aggregatedResult(orders, null, null)).thenReturn(expectedAggregatedResult);

        val result = aggregationEndpoint.aggregatedResult(orders, null, null);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
        );
    }

    @Test
    public void testAggregatedResult_whenNonNullTrackOrders() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val orders = List.of(12345);
        val expectedAggregatedResult = AggregatedResult.builder()
                .track(Map.of("12345", AggregatedResult.InnerEnum.DELIVERED))
                .build();

        val expectedResponse = ResponseEntity.ok(expectedAggregatedResult);

        //WHEN
        when(aggregationService.aggregatedResult(null, orders, null)).thenReturn(expectedAggregatedResult);

        val result = aggregationEndpoint.aggregatedResult(null, orders, null);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
        );
    }

    @Test
    public void testAggregatedResult_whenNonNullCountryCodes() throws ExecutionException, InterruptedException, TimeoutException {
        //GIVEN
        val countryCodes = List.of("US");
        val expectedAggregatedResult = AggregatedResult.builder()
                .pricing(Map.of("US", BigDecimal.ONE))
                .build();

        val expectedResponse = ResponseEntity.ok(expectedAggregatedResult);

        //WHEN
        when(aggregationService.aggregatedResult(null, null, countryCodes)).thenReturn(expectedAggregatedResult);

        val result = aggregationEndpoint.aggregatedResult(null, null, countryCodes);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
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

        val expectedResponse = ResponseEntity.ok(expectedAggregatedResult);

        //WHEN
        when(aggregationService.aggregatedResult(orders, orders, countryCodes)).thenReturn(expectedAggregatedResult);

        val result = aggregationEndpoint.aggregatedResult(orders, orders, countryCodes);

        //ASSERT
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expectedResponse, result)
        );
    }
}
