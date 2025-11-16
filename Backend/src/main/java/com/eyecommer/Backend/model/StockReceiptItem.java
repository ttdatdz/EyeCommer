package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Stock_receipt_item")
@Getter
@Setter
public class StockReceiptItem extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "stock_receipt_id")
    private StockReceipts stockReceipt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;
}

