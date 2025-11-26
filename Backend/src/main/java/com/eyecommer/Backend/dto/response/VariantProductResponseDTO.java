package com.eyecommer.Backend.dto.response;

import lombok.Data;
import java.util.Set;

@Data
public class VariantProductResponseDTO {
    private Long id;
    private String sku;
    private Double price;
    private Integer stock;

    // TRƯỜNG MỚI: Danh sách các thuộc tính (ví dụ: Màu Đỏ, Size L)
    private Set<AttributeResponseDTO> attributes;
    private Set<VariantImageResponseDTO> images;
}