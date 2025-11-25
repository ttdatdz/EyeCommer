package com.eyecommer.Backend.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String status;
    private String thumbnailUrl;
    private String shortDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Mối quan hệ 1-N: Danh sách các biến thể
    private Set<VariantProductResponseDTO> variants;

    //  Danh sách các danh mục (bao gồm cả trạng thái isDefault)
    private Set<CategoryResponseDTO> categories;
}