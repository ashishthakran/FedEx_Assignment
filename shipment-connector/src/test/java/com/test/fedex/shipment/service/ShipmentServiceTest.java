package com.test.fedex.shipment.service;

import com.test.fedex.common.config.ApiConfigProperties;
import com.test.fedex.shipment.generated.api.ShipmentServiceApi;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShipmentServiceTest {

    private ApiConfigProperties apiConfigProperties = mock(ApiConfigProperties.class);
    private ShipmentServiceApi shipmentServiceApi = mock(ShipmentServiceApi.class);

    private ShipmentService shipmentService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(apiConfigProperties, shipmentServiceApi);
        shipmentService = new ShipmentService(apiConfigProperties, shipmentServiceApi);
    }

    @Test
    public void testShipmentProducts_whenNullInput() {
        assertEquals(Collections.emptyMap(), shipmentService.shipmentProducts(null));
    }

    @Test
    public void testShipmentProducts_whenShipmentApiThrowsException() {

        //GIVEN
        val orderNumbers = List.of(123456);

        //WHEN
        when(shipmentServiceApi.getShipmentProducts(123456)).thenThrow(HttpServerErrorException.class);
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);

        val result = shipmentService.shipmentProducts(orderNumbers);

        //ASSERT
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    public void testShipmentProducts() {

        //GIVEN
        val orderNumbers = List.of(123456);

        //WHEN
        when(shipmentServiceApi.getShipmentProducts(123456)).thenReturn(List.of("BOX", "ENVELOPE"));
        when(apiConfigProperties.getReadTimeout()).thenReturn(5);

        val result = shipmentService.shipmentProducts(orderNumbers);

        //ASSERT
        assertAll(
                () -> assertTrue(result.containsKey("123456")),
                () -> assertEquals(List.of("BOX", "ENVELOPE"), result.get("123456"))
        );
    }
}
