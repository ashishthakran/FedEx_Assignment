package com.test.fedex.pricing.model;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryBasePricingTest {

    @Test
    public void testGetters() {
        //GIVEN
        val countryBasePricing = CountryBasePricing.builder()
                .countryCode("US")
                .price(BigDecimal.ONE)
                .build();

        //ASSERT
        assertAll(
                () -> assertEquals("US", countryBasePricing.getCountryCode()),
                () -> assertEquals(BigDecimal.ONE, countryBasePricing.getPrice())
        );
    }
}
