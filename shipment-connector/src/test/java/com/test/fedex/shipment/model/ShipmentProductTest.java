package com.test.fedex.shipment.model;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShipmentProductTest {

    @Test
    public void testGetters() {

        //GIVEN
        val shipmentProduct = ShipmentProduct.builder()
                .orderNumber("123456")
                .products(List.of("1111", "2222"))
                .build();

        //ASSERT
        //ASSERT
        assertAll(
                () -> assertEquals("123456", shipmentProduct.getOrderNumber()),
                () -> assertEquals(List.of("1111", "2222"), shipmentProduct.getProducts())
        );
    }
}
