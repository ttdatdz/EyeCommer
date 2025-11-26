package com.eyecommer.Backend.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class VariantProductUpdateDTO {

    private Long id;

    private String sku;
    private Double price;
    private Integer stock;

    // IDs của các thuộc tính (Attribute) tạo nên SKU này.
    // Danh sách này sẽ GHI ĐÈ lên các VariantProductAttribute cũ.
    private List<Long> variantAttributeIds;
    List<VariantImageRequestDTO> images;
    // Cần thêm List<VariantImageRequestDTO> images nếu bạn cập nhật ảnh qua đây.
}