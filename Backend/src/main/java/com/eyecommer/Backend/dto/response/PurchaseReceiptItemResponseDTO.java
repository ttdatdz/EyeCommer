package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class PurchaseReceiptItemResponseDTO {
    private String sku;
    private String productName;
    private Integer quantity;
    private Double price;
}
