package com.eyecommer.Backend.model;

import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Orders")
@Getter
@Setter
public class Order extends AbstractEntity<Long> {

@Column(name = "order_code", unique = true, nullable = false)
private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    // ====== Lifecycle ======
    private LocalDateTime confirmedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime deliveredAt;

    // ====== Relations ======
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Payment> payments;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipments shipments;


    @OneToMany(mappedBy = "usedOrder")
    private Set<VoucherUser> usedVouchers;
}
