package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.utils.InventoryStatus;
import lombok.Data;

@Data
public class InventoryResponseDTO {

    private Long variantId;
    private String productName;
    private String sku;

    private Integer stock;          // tồn thực
    private Integer reservedStock;  // tồn giữ chỗ
    private Integer availableStock; // stock - reserved

    private Double price;           // giá bán (hoặc giá nhập nếu muốn)
    private InventoryStatus status;          // IN_STOCK | LOW_STOCK | OUT_OF_STOCK
}
