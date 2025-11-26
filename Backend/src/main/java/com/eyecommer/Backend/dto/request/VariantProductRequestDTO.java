package com.eyecommer.Backend.dto.request;

import com.eyecommer.Backend.dto.response.VariantImageResponseDTO;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class VariantProductRequestDTO {
    private String sku;
    private Double price; // Giá bán của biến thể này
    private Integer stock; // Tồn kho của biến thể này

    private List<Long> variantAttributeIds;
    // TRƯỜNG MỚI: Danh sách hình ảnh liên quan đến SKU
    private Set<VariantImageRequestDTO> images;
}