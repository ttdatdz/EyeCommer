package com.eyecommer.Backend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private Double totalPrice;
    private int totalQuantity;
}
