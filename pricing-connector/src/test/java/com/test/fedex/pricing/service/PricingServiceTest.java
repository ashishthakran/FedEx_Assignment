package com.test.fedex.pricing.service;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.pricing.generated.api.PricingServiceApi;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PricingServiceTest {

    private ApiConfigProperties apiConfigProperties = mock(ApiConfigProperties.class);
    private PricingServiceApi pricingServiceApi = mock(PricingServiceApi.class);

    private PricingService pricingService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(apiConfigProperties, pricingServiceApi);
        pricingService = new PricingService(apiConfigProperties, pricingServiceApi);
    }

    @Test
    public void testCountryBasePricing_whenNullInput() {
        assertEquals(Collections.emptyMap(), pricingService.countryBasePricing(null));
    }

    @Test
    public void testCountryBasePricing_whenPricingApiThrowsException() {

        //GIVEN
        val countryCodes = List.of("US");

        //WHEN
        when(pricingServiceApi.getBasePricing("US")).thenThrow(HttpServerErrorException.class);
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);

        val result = pricingService.countryBasePricing(countryCodes);

        //ASSERT
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    public void testCountryBasePricing() {

        //GIVEN
        val countryCodes = List.of("US");

        //WHEN
        when(pricingServiceApi.getBasePricing("US")).thenReturn(BigDecimal.ONE);
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);

        val result = pricingService.countryBasePricing(countryCodes);

        //ASSERT
        assertAll(
                () -> assertTrue(result.containsKey("US")),
                () -> assertEquals(BigDecimal.ONE, result.get("US"))
        );
    }
}
