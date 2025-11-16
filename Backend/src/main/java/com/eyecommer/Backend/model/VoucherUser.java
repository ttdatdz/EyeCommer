package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Voucher_User")
@Getter
@Setter
public class VoucherUser extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "used_order_id")
    private Order usedOrder;

    @Column(name = "used_date")
    private Date usedDate;

    @Column(name = "status")
    private String status;
}

