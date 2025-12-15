package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.dto.request.OrderItemSnapshotDTO;
import com.eyecommer.Backend.utils.PaymentStatus;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutResponseDTO {
    private String orderCode;
    private Double totalAmount;
    private Double finalAmount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String vnPayUrl; // chỉ dùng khi paymentMethod = VNPAY
    private List<OrderItemSnapshotDTO> items;
}