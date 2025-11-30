package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    // Thời điểm voucher được sử dụng (khi đặt hàng / checkout) — có thể null nếu chỉ mới claim
    @Column(name = "used_date")
    private LocalDateTime usedDate;

    // Thời điểm user claim/nhận voucher — khác column với usedDate
    @Column(name = "claim_date")
    private LocalDateTime claimDate;

    @Column(name = "status")
    private String status;
}

