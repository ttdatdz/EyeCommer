package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.mapper.OrderMapper;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.service.GHNService;
import com.eyecommer.Backend.service.OrderService;
import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import com.eyecommer.Backend.utils.SnapshotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final GHNService ghnService;
    private final ShipmentRepository shipmentRepository;

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

    @Transactional
    @Override
    public void confirmOrder(ConfirmOrderRequestDTO request) {

        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 1️⃣ Check trạng thái
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be confirmed");
        }

        // 2️⃣ Check shipment tồn tại
        if (shipmentRepository.existsByOrder(order)) {
            throw new RuntimeException("Shipment already created");
        }

        // 3️⃣ Gọi GHN tạo shipment (FAIL → rollback)
        ghnService.createShipment(order);

        // 4️⃣ Update order
        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());

        orderRepository.save(order);
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
