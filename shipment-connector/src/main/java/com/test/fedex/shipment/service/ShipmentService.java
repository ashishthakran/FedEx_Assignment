package com.test.fedex.shipment.service;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.common.utils.LogUtils;
import com.test.fedex.shipment.generated.api.ShipmentServiceApi;
import com.test.fedex.shipment.model.ShipmentProduct;
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
 * This class will call shipment backend api and get the response.
 * It will act as an abstraction so that not to call Shipment service directly.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ShipmentService {

    private final ApiConfigProperties apiConfigProperties;
    private final ShipmentServiceApi shipmentServiceApi;

    /**
     * It will return shipment products for the list of order numbers.
     * If backend shipment service is unable to get products for a selected order number within 5 seconds then
     * call to backend shipment service will be timed-out and the selected order will be excluded.
     *
     * @param orderNumbers List of order numbers
     * @return Map of objects
     */
    public Map<String, List<String>> shipmentProducts(final List<Integer> orderNumbers) {
        return Optional.ofNullable(orderNumbers)
                .orElse(Collections.emptyList())
                .parallelStream()
                .map(this::getProducts)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ShipmentProduct::getOrderNumber, ShipmentProduct::getProducts));
    }

    /**
     * This method will call backend shipment service for a selected order.
     * Call to backend shipment service will be timed out if backend shipment service doesn't return the response within 5 seconds.
     *
     * @param orderNumber Order Number
     * @return ShipmentProduct
     */
    private ShipmentProduct getProducts(final Integer orderNumber) {

        //Stop watch to calculate the total amount of time taken from backend shipment service.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            CompletableFuture<List<String>> shipmentProductsCompletableFuture = CompletableFuture.supplyAsync(() ->
                    shipmentServiceApi.getShipmentProducts(orderNumber));

            //Read the response from backend shipment service. Time out the request if it takes more than 5 seconds
            List<String> products = shipmentProductsCompletableFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS);

            return ShipmentProduct.builder()
                    .orderNumber(String.valueOf(orderNumber))
                    .products(products)
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        stopWatch.stop();
        LogUtils.recordTimeOuts("ShipmentProducts", stopWatch, apiConfigProperties.getReadTimeout());
        return null;
    }
}
