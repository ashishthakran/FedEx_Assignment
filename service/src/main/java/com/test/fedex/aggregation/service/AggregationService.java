package com.test.fedex.aggregation.service;

import com.test.fedex.aggregation.generated.model.AggregatedResult;
import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.common.enums.TrackStatus;
import com.test.fedex.pricing.service.PricingService;
import com.test.fedex.shipment.service.ShipmentService;
import com.test.fedex.trackstatus.service.TrackStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class will call all backend services (Shipment, TrackStatus, BasePricing)
 * and aggregate all backend services responses.
 *
 * In case there is an error while fetching data from any of the API then that response will be skipped.
 * All Backend Services must return the response within 5 seconds, otherwise the respected call to any backend service
 * will be timed out and that particular input data will not be included in the response.
 *
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AggregationService {

    private final ApiConfigProperties apiConfigProperties;
    private final ShipmentService shipmentService;
    private final TrackStatusService trackStatusService;
    private final PricingService pricingService;

    /**
     * It will call all backend services (Shipment, TrackStatus, BasePricing) and aggregate all backend services responses.
     * @param shipmentsOrderNumbers
     * @param trackOrderNumbers
     * @param pricingCountryCodes
     * @return AggregatedResult
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public AggregatedResult aggregatedResult(List<Integer> shipmentsOrderNumbers,
                                             List<Integer> trackOrderNumbers,
                                             List<String> pricingCountryCodes) throws ExecutionException, InterruptedException, TimeoutException {
        val aggregatedResult = new AggregatedResult();
        val shipmentProductsFuture = CompletableFuture.supplyAsync(() -> shipmentService.shipmentProducts(shipmentsOrderNumbers));
        val trackStatusFuture = CompletableFuture.supplyAsync(() -> trackStatusService.trackingStatus(trackOrderNumbers));
        val pricingFuture = CompletableFuture.supplyAsync(() -> pricingService.countryBasePricing(pricingCountryCodes));

        shipmentProductsFuture.completeOnTimeout(Collections.emptyMap(), apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS)
                .thenAccept(products -> aggregatedResult.setShipments(products));

        trackStatusFuture.completeOnTimeout(Collections.emptyMap(), apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS)
                .thenAccept(trackStatus -> {
                    trackStatus.entrySet().stream()
                            .findFirst()
                            .ifPresent(status -> aggregatedResult.setTrack(Map.of(status.getKey(), AggregatedResult.InnerEnum.fromValue(status.getValue().name()))));
                });

        pricingFuture.completeOnTimeout(Collections.emptyMap(), apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS)
                .thenAccept(pricing -> aggregatedResult.setPricing(pricing));

        val completableFutureAllOf = CompletableFuture.allOf(shipmentProductsFuture, trackStatusFuture, pricingFuture);

        val aggregatedResultFuture =
                completableFutureAllOf
                        .thenApply((voidInput) -> this.prepareResult(shipmentProductsFuture, trackStatusFuture, pricingFuture))
                        .completeOnTimeout(AggregatedResult.builder().build(), apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS);

        return aggregatedResultFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS);
    }

    /**
     * This method will aggregate responses from all backend services.
     * @param shipmentProductsFuture
     * @param trackStatusFuture
     * @param pricingFuture
     * @return
     */
    private AggregatedResult prepareResult(CompletableFuture<Map<String, List<String>>> shipmentProductsFuture,
                                           CompletableFuture<Map<String, TrackStatus>> trackStatusFuture,
                                           CompletableFuture<Map<String, BigDecimal>> pricingFuture) {
        try {
            Map<String, AggregatedResult.InnerEnum> trackStatus = trackStatusFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS)
                    .entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), AggregatedResult.InnerEnum.fromValue(entry.getValue().name())) )
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

            return AggregatedResult.builder()
                    .shipments(shipmentProductsFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS))
                    .track(trackStatus)
                    .pricing(pricingFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS))
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return AggregatedResult.builder().build();
    }
}
