package com.test.fedex.pricing.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains base pricing details of a country.
 */
@Builder
@Getter
public class CountryBasePricing {

    private String countryCode;
    private BigDecimal price;
}
