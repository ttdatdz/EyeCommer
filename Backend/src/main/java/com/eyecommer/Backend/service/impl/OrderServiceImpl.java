package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.mapper.OrderMapper;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.AddressRepository;
import com.eyecommer.Backend.repository.OrderRepository;
import com.eyecommer.Backend.repository.OrderSnapshotRepository;
import com.eyecommer.Backend.repository.VariantProductRepository;
import com.eyecommer.Backend.service.GHNService;
import com.eyecommer.Backend.service.OrderService;
import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import com.eyecommer.Backend.utils.SnapshotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final GHNService ghnService;

    @Override
    public OrderDetailResponseDTO getOrderDetail(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDetailDTO(order);
    }

    @Override
    public List<OrderSummaryResponseDTO> getMyOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public List<OrderSummaryResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public void confirmOrder(ConfirmOrderRequestDTO request) {
        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be confirmed");
        }

        // 1️⃣ Gọi GHN tạo shipment
        ghnService.createShipment(order, request);

        // 2️⃣ Update order
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
    }

    @Override
    public void cancelOrder(CancelOrderRequestDTO request) {
        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCanceledAt(LocalDateTime.now());

        // (Optional) refund logic nếu cần
    }

}
