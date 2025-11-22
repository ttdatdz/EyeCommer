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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_product_id")
    private VariantProduct variantProduct;


    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;
}

