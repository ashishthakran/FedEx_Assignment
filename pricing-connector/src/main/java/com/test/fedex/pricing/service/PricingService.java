package com.test.fedex.pricing.service;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.common.utils.LogUtils;
import com.test.fedex.pricing.generated.api.PricingServiceApi;
import com.test.fedex.pricing.model.CountryBasePricing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
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
 * This class will call pricing backend api and get the response.
 * It will act as an abstraction so that not to call Pricing service directly
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PricingService {

    private final ApiConfigProperties apiConfigProperties;
    private final PricingServiceApi pricingServiceApi;

    /**
     * It will return base pricing for the list of ISO country codes.
     * If backend pricing service is unable to get pricing for a selected country code within 5 seconds then
     * call to backend pricing service will be timed-out and the selected country code will be excluded.
     *
     * @param countryCodes List of ISO country codes
     * @return Map of objects
     */
    public Map<String, BigDecimal> countryBasePricing(final List<String> countryCodes) {
        return Optional.ofNullable(countryCodes)
                .orElse(Collections.emptyList())
                .parallelStream()
                .map(this::getBasePricing)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(CountryBasePricing::getCountryCode, CountryBasePricing::getPrice));
    }

    /**
     * This method will call backend pricing service for a selected country code.
     * Call to backend pricing service will be timed out if backend pricing service doesn't return the response within 5 seconds.
     *
     * @param countryCode ISO Country code
     * @return CountryBasePricing
     */
    private CountryBasePricing getBasePricing(final String countryCode) {

        //Stop watch to calculate the total amount of time taken from backend pricing service.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            CompletableFuture<BigDecimal> pricingCompletableFuture = CompletableFuture.supplyAsync(() ->
                    pricingServiceApi.getBasePricing(countryCode));

            //Read the response from backend pricing service. Time out the request if it takes more than 5 seconds
            BigDecimal price = pricingCompletableFuture.get(apiConfigProperties.getReadTimeout(), TimeUnit.SECONDS);

            return CountryBasePricing.builder()
                    .countryCode(countryCode)
                    .price(price)
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        stopWatch.stop();
        LogUtils.recordTimeOuts("BasePricing", stopWatch, apiConfigProperties.getReadTimeout());
        return null;
    }
}
