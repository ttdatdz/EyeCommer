package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;

import java.util.List;

public interface OrderService {
    OrderDetailResponseDTO getOrderDetail(String orderCode);

    List<OrderSummaryResponseDTO> getMyOrders(Long userId);

    List<OrderSummaryResponseDTO> getAllOrders();

    void confirmOrder(ConfirmOrderRequestDTO request);

    void cancelOrder(CancelOrderRequestDTO request);
}
