package com.eyecommer.Backend.model;

import com.eyecommer.Backend.utils.PurchaseReceiptStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Stock_receipts")
@Getter
@Setter
public class StockReceipts extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseReceiptStatus status;

    @Column(name = "receipt_date")
    private LocalDateTime receiptDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StockReceiptItem> items = new HashSet<>();
}
