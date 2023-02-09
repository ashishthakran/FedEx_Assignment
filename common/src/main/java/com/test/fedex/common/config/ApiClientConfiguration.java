package com.test.fedex.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains API Client Configuration.
 */
@Configuration
@Getter
@RequiredArgsConstructor
public class ApiClientConfiguration {

    private final ApiConfigProperties apiConfigProperties;

    /**
     * OpenAPI Codegen generates API Client. API Client connection and read time-out need to be overridden
     * in order to meet backend services SLA.
     *
     * @param restTemplateBuilder
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(apiConfigProperties.getConnectionTimeout()))
                .setReadTimeout(Duration.ofSeconds(apiConfigProperties.getReadTimeout()))
                .build();
    }
}
