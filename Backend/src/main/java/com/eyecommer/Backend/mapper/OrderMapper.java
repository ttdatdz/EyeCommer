package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderItemResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderSummaryResponseDTO toSummaryDTO(Order order) {
        OrderSummaryResponseDTO dto = new OrderSummaryResponseDTO();
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    public OrderDetailResponseDTO toDetailDTO(Order order) {
        OrderDetailResponseDTO dto = new OrderDetailResponseDTO();
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setConfirmedAt(order.getConfirmedAt());
        dto.setCanceledAt(order.getCanceledAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        Address address = order.getAddress();
        dto.setReceiverName(address.getReceiverName());
        dto.setReceiverPhone(address.getReceiverPhone());
        dto.setFullAddress(address.getAddressDetail());

        List<OrderItemResponseDTO> items = order.getOrderItems().stream().map(item -> {
            OrderItemResponseDTO i = new OrderItemResponseDTO();
            i.setProductId(item.getVariantProduct().getProduct().getId());
            i.setProductName(item.getVariantProduct().getProduct().getName());
            i.setVariantName(item.getVariantProduct().getSku());
            i.setPrice(item.getPrice());
            i.setQuantity(item.getQuantity());
            i.setLineTotal(item.getPrice() * item.getQuantity());
            return i;
        }).toList();

        dto.setItems(items);
        return dto;
    }
}

