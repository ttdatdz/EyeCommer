package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_item_snapshot")
@Getter
@Setter
public class OrderItemSnapshot extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "order_snapshot_id")
    private OrderSnapshot orderSnapshot;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "variant_name")
    private String variantName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "price_at_purchase")
    private Double priceAtPurchase;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "line_total")
    private Double lineTotal;
}
