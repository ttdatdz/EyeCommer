package com.eyecommer.Backend.mapper;


import com.eyecommer.Backend.dto.request.OrderItemSnapshotDTO;
import com.eyecommer.Backend.dto.response.CheckoutResponseDTO;
import com.eyecommer.Backend.model.OrderSnapshot;
import com.eyecommer.Backend.model.OrderItemSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderSnapshotMapper {

    public CheckoutResponseDTO toCheckoutResponseDTO(OrderSnapshot snapshot, String vnPayUrl) {
        CheckoutResponseDTO dto = new CheckoutResponseDTO();
        dto.setOrderCode(snapshot.getOrderCode());
        dto.setTotalAmount(snapshot.getTotalAmount());
        dto.setFinalAmount(snapshot.getFinalAmount());
        dto.setPaymentMethod(snapshot.getPaymentMethod());
        dto.setPaymentStatus(snapshot.getPaymentStatus());
        dto.setVnPayUrl(vnPayUrl);

        List<OrderItemSnapshotDTO> items = snapshot.getItems().stream().map(this::toItemDTO).collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    private OrderItemSnapshotDTO toItemDTO(OrderItemSnapshot item) {
        OrderItemSnapshotDTO dto = new OrderItemSnapshotDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setVariantId(item.getVariantId());
        dto.setVariantName(item.getVariantName());
        dto.setImageUrl(item.getImageUrl());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setQuantity(item.getQuantity());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }
}
