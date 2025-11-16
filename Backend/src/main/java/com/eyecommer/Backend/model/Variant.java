package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Variant")
@Getter
@Setter
public class Variant extends AbstractEntity<Long> {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "variant")
    private Set<VariantProduct> variantProducts;

    @OneToMany(mappedBy = "variant")
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "variant")
    private Set<StockReceiptItem> stockReceiptItems;
}
