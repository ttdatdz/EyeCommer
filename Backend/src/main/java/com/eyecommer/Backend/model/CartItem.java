package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Cart_Item")
@Getter
@Setter
public class CartItem extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "variant_product_id")
    private VariantProduct variantProduct;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price") // giá lúc thêm vào giỏ
    private Double price;
}
