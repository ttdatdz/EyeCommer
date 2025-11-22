package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Variant_Product_Attribute")
@Getter
@Setter
// Entity trung gian này là đơn vị chi tiết để tạo nên một SKU
public class VariantProductAttribute extends AbstractEntity<Long> {

    // Khóa ngoại 1: Liên kết N-1 tới VariantProduct (SKU)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_product_id", nullable = false)
    private VariantProduct variantProduct;

    // Khóa ngoại 2: Liên kết N-1 tới Variant (Thuộc tính)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    // Ví dụ: Có thể thêm trường metadata (nếu cần)
    // @Column(name = "display_order")
    // private Integer displayOrder;
}