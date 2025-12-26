package com.eyecommer.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Voucher")
@Getter
@Setter
public class Voucher extends AbstractEntity<Long> {
    @Column(name = "code", unique = true) // Đảm bảo code là duy nhất
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "max_usage")
    private Integer maxUsage; // SỐ LƯỢNG VOUCHER TỐI ĐA ĐƯỢC PHÁT HÀNH

    @Column(name = "current_usage")
    private Integer currentUsage = 0; // SỐ LƯỢNG ĐÃ ĐƯỢC NGƯỜI DÙNG NHẬN/SỬ DỤNG

    @OneToMany(mappedBy = "voucher")
    private Set<VoucherUser> users;


}

