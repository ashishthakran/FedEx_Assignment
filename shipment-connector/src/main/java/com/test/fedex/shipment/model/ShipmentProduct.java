package com.test.fedex.shipment.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author Aashish Thakran
 * @version 1.0
 *
 * This class contains list of shipment product of an order.
 */
@Builder
@Getter
public class ShipmentProduct {

    private String orderNumber;
    private List<String> products;
}
