package com.test.fedex.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains API configurations.
 */
@Configuration
@Getter
@Setter
public class ApiConfigProperties {

    @Value("${api.config.connect-timeout}")
    private Integer connectionTimeout;

    @Value("${api.config.read-timeout}")
    private Integer readTimeout;

    @Value("${api.shipment.base-path}")
    private String shipmentApiBasePath;

    @Value("${api.pricing.base-path}")
    private String pricingApiBasePath;

    @Value("${api.trackstatus.base-path}")
    private String trackStatusApiBasePath;
}
