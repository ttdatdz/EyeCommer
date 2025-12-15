package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderSummaryResponseDTO {
    private String orderCode;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private Double totalAmount;
    private LocalDateTime createdAt;
}
