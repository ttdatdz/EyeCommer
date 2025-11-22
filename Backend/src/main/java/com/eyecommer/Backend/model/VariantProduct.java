package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Variant_Product")
@Getter
@Setter
public class VariantProduct extends AbstractEntity<Long> {
    @Column(name = "sku", unique = true)
    private String sku; //mã của mỗi biến thể
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "price") // Giá bán của biến thể này
    private Double price;

    @Column(name = "stock") // Tồn kho của biến thể này
    private Integer stock;

    // Ảnh của Biến thể
    @OneToMany(mappedBy = "variantProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariantImage> images;

    // SỬA: Thay thế @ManyToMany cũ bằng @OneToMany tới bảng trung gian mới
    // Một SKU (VariantProduct) được tạo thành từ Nhiều (N) Attribute Value.
    @OneToMany(mappedBy = "variantProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariantProductAttribute> attributes; // Đổi tên để rõ nghĩa hơn

    @OneToMany(mappedBy = "variantProduct")
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "variantProduct")
    private Set<StockReceiptItem> stockReceiptItems;
}
