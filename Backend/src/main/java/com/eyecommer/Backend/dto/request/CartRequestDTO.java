package com.eyecommer.Backend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CartRequestDTO {
    private List<CartItemRequestDTO> items;
}
