package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class VariantProductRequestDTO {
    private String sku;
    private Double price; // Giá bán của biến thể này
    private Integer stock; // Tồn kho của biến thể này

    // Giả định bạn có DTO cho Attributes và Images,
    // tạm thời đơn giản hóa cho ví dụ này.
    // private List<VariantAttributeRequest> attributes;
    // private List<VariantImageRequest> images;
}