package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class CartItemResponseDTO {
    private Long id;
    private Long variantProductId;
    private String variantName; // có thể lấy từ product + variant info
    private Integer quantity;
    private Double price;
}