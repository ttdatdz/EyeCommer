package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Stock_receipts")
@Getter
@Setter
public class StockReceipts extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "receipt_date")
    private Date receiptDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @OneToMany(mappedBy = "stockReceipt")
    private Set<StockReceiptItem> items;
}
