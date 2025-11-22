package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Variant_Image")
@Getter
@Setter
public class VariantImage extends AbstractEntity<Long> {

    // URL ảnh từ Cloudinary
    @Column(name = "image_url")
    private String imageUrl;

    // Ảnh chính (đại diện) của biến thể này
    @Column(name = "is_thumbnail")
    private Boolean isThumbnail = false;

    // Liên kết với biến thể cụ thể (VariantProduct)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_product_id")
    private VariantProduct variantProduct;
}
