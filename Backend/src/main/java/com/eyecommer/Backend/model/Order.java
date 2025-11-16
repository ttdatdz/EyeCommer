package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Orders")
@Getter
@Setter
public class Order extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "order")
    private Set<Payment> payments;

    @OneToMany(mappedBy = "order")
    private Set<Shipments> shipments;

    @OneToMany(mappedBy = "usedOrder")
    private Set<VoucherUser> usedVouchers;
}
