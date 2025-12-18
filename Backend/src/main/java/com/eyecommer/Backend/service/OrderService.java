package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;

import java.util.List;

public interface OrderService {
    OrderDetailResponseDTO getOrderDetail(String orderCode);

    PageResponse<?> getMyOrders(Long userId, int pageNo, int pageSize, String sortBy, String[] search);

    PageResponse<?> getAllOrders(int pageNo, int pageSize, String sortBy, String[] search);

    void confirmOrder(ConfirmOrderRequestDTO request);

    void cancelOrder(String orderCode, String reason);
}
