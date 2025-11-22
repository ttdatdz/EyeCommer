package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Order_item")
@Getter
@Setter
public class OrderItem extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_product_id")
    private VariantProduct variantProduct;


    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    @OneToMany(mappedBy = "orderItem")
    private Set<Review> reviews;
}
