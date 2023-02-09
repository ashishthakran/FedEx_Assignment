package com.test.fedex.trackstatus.service;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.common.enums.TrackStatus;
import com.test.fedex.common.utils.LogUtils;
import com.test.fedex.track.generated.api.TrackServiceApi;
import com.test.fedex.trackstatus.model.OrderTrackStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class will call TrackStatus backend api and get the response.
 * It will act as an abstraction so that not to call TrackStatus service directly
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TrackStatusService {

    private final ApiConfigProperties apiConfigProperties;
    private final TrackServiceApi trackServiceApi;

    /**
     * It will return track status for the list of orders.
     * If backend TrackStatus service is unable to get status for a selected order within 5 seconds then
     * call to backend TrackStatus service will be timed-out and the selected order number will be excluded.
     *
     * @param orderNumbers List of Order Numbers
     * @return Map of objects
     */
    public Map<String, TrackStatus> trackingStatus(final List<Integer> orderNumbers) {
        return Optional.ofNullable(orderNumbers)
                .orElse(Collections.emptyList())
                .parallelStream()
                .map(this::getStatus)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(OrderTrackStatus::getOrderNumber, OrderTrackStatus::getStatus));
    }

    /**
     * This method will call backend TrackStatus service for a selected order number.
     * Call to backend TrackStatus service will be timed out if backend TrackStatus service doesn't return the response within 5 seconds.
     *
     * @param orderNumber Order Number
     * @return TrackStatus
     */
    private OrderTrackStatus getStatus(final Integer orderNumber) {

        //Stop watch to calculate the total amount of time taken from backend TrackStatus service.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            CompletableFuture<String> trackStatusCompletableFuture = CompletableFuture.supplyAsync(() ->
                    (String) trackServiceApi.getTrackingStatus(orderNumber));

            //Read the response from backend TrackStatus service. Time out the request if it takes more than 5 seconds
            String status = trackStatusCompletableFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS);

            return OrderTrackStatus.builder()
                    .orderNumber(String.valueOf(orderNumber))
                    .status(TrackStatus.getStatus(status))
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        stopWatch.stop();
        LogUtils.recordTimeOuts("TrackStatus", stopWatch, apiConfigProperties.getReadTimeout());
        return null;
    }
}
