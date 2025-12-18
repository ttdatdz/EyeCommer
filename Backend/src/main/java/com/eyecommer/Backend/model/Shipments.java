package com.eyecommer.Backend.model;

import com.eyecommer.Backend.utils.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Shipments")
@Getter
@Setter
public class Shipments extends AbstractEntity<Long> {
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;


    @Column(name = "carrier", nullable = false)
    private String carrier;


    @Column(name = "shipment_code", nullable = false, unique = true)
    private String shipmentCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;


    @Column(name = "shipping_fee")
    private Long shippingFee;

    @Column(name = "ship_date")
    private LocalDateTime shipDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "expected_delivery_time")
    private LocalDateTime expectedDeliveryTime;

}
