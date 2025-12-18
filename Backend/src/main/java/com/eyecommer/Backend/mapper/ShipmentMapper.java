package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.utils.ShipmentStatus;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    public ShipmentStatus mapGHNStatus(String ghnStatus) {
        return switch (ghnStatus) {
            case "picking", "picked" -> ShipmentStatus.PICKED_UP;
            case "delivering" -> ShipmentStatus.SHIPPING;
            case "delivered" -> ShipmentStatus.DELIVERED;
            case "delivery_fail" -> ShipmentStatus.FAILED;
            case "return" -> ShipmentStatus.RETURNED;
            case "cancel" -> ShipmentStatus.CANCELLED;
            default -> ShipmentStatus.CREATED;
        };
    }
}
