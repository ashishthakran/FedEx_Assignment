package com.test.fedex.aggregation.api;

import com.test.fedex.aggregation.generated.api.AggregationApi;
import com.test.fedex.aggregation.generated.model.AggregatedResult;
import com.test.fedex.aggregation.service.AggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class will act as REST endpoint implementation for Aggregated Service.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
public class AggregationEndpoint implements AggregationApi {

    private final AggregationService aggregationService;

    /**
     * It will return aggregated result (of shipment products, track status and base pricing) for list of orders and countries.
     *
     * @param shipmentsOrderNumbers Shipment Order Numbers (optional)
     * @param trackOrderNumbers Track Order Numbers (optional)
     * @param pricingCountryCodes Pricing Country Codes (optional)
     * @return AggregatedResult
     */
    @Override
    public ResponseEntity<AggregatedResult> aggregatedResult(List<Integer> shipmentsOrderNumbers,
                                                             List<Integer> trackOrderNumbers,
                                                             List<String> pricingCountryCodes) {
        try {
            return ResponseEntity.ok(aggregationService.aggregatedResult(shipmentsOrderNumbers, trackOrderNumbers, pricingCountryCodes));
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
