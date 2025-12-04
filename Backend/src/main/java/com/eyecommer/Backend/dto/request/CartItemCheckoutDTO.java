package com.eyecommer.Backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemCheckoutDTO {

    private Long cartItemId;   // Id item trong cart
    private Integer quantity;  // Số lượng muốn mua (<= quantity trong cart)
}