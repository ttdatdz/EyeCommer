package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class CartItemRequestDTO {
    private Long variantProductId;
    private Integer quantity;
}