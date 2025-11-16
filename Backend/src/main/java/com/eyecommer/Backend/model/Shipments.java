package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Shipments")
@Getter
@Setter
public class Shipments extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "status")
    private String status;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "ship_date")
    private Date shipDate;

    @Column(name = "delivery_date")
    private Date deliveryDate;
}
