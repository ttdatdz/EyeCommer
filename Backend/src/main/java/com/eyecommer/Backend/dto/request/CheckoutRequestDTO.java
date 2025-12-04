package com.eyecommer.Backend.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequestDTO {
    private Long userId;
    private Long addressId;
    private String paymentMethod; // COD / VNPAY
    private Long voucherId;       // optional
    private List<CartItemCheckoutDTO> items;
}
