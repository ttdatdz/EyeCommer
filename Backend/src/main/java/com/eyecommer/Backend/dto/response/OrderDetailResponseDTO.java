package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDetailResponseDTO {
    private String orderCode;
    private OrderStatus status;
    private PaymentStatus paymentStatus;

    private Double totalAmount;

    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime deliveredAt;

    private String receiverName;
    private String receiverPhone;
    private String fullAddress;

    private List<OrderItemResponseDTO> items;
}

