package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class PurchaseReceiptItemRequestDTO {
    private Long variantProductId;
    private Integer quantity;
    private Double price;
}
