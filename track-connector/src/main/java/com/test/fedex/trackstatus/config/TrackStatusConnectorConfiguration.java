package com.test.fedex.trackstatus.config;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.track.generated.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains TrackStatus Connector configuration.
 * This configuration will be active when aggregation service is deployed in docker container.
 */
@Configuration
@Profile("docker")
@RequiredArgsConstructor
public class TrackStatusConnectorConfiguration {

    private final ApiConfigProperties apiConfigProperties;
    private final ApplicationContext applicationContext;

    /**
     * This method will be executed after {@link TrackStatusConnectorConfiguration} initialization.
     * It's overriding pricing api base path while running in docker container.
     */
    @PostConstruct
    public void postConstruct() {
        ApiClient apiClient = applicationContext.getBean(ApiClient.class);
        apiClient.setBasePath(apiConfigProperties.getTrackStatusApiBasePath());
    }
}
