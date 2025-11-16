package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Payment")
@Getter
@Setter
public class Payment extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_date")
    private Date paymentDate;
}
