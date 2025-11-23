package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Product_Category")
@Getter
@Setter
public class ProductCategory extends AbstractEntity<Long> {

    // Khóa ngoại 1: Liên kết N-1 tới Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Khóa ngoại 2: Liên kết N-1 tới Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Có thể thêm các trường metadata tại đây
    @Column(name = "is_default") // Ví dụ: Đánh dấu danh mục chính
    private Boolean isDefault = false;
}