package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Product")
@Getter
@Setter
public class Product extends AbstractEntity<Long> {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "status")
    private String status;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "short_description")
    private String shortDescription;


    // Quan hệ 1-N tới bảng trung gian ProductCategory
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductCategory> productCategories;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariantProduct> variants;

//    @OneToMany(mappedBy = "product")
//    private Set<OrderItem> orderItems;
//
//    @OneToMany(mappedBy = "product")
//    private Set<StockReceiptItem> stockReceiptItems;

}
