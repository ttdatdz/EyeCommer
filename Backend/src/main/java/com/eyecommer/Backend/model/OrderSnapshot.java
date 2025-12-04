package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_snapshot")
@Getter
@Setter
public class OrderSnapshot extends AbstractEntity<Long> {

    @Column(name = "order_code", unique = true)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "payment_method")
    private String paymentMethod; // COD / VNPAY

    @Column(name = "payment_status")
    private String paymentStatus; // PENDING / PAID / UNPAID

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "voucher_id")
    private Long voucherId;

    @Column(name = "voucher_code")
    private String voucherCode;

    @Column(name = "voucher_discount_amount")
    private Double voucherDiscountAmount;

    @Column(name = "final_amount")
    private Double finalAmount;

    @OneToMany(mappedBy = "orderSnapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemSnapshot> items = new HashSet<>();

    public void addItem(OrderItemSnapshot item) {
        if (items == null) items = new HashSet<>();
        items.add(item);
        item.setOrderSnapshot(this);
    }
}
