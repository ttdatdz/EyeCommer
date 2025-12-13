package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.AddressRepository;
import com.eyecommer.Backend.repository.OrderRepository;
import com.eyecommer.Backend.repository.OrderSnapshotRepository;
import com.eyecommer.Backend.repository.VariantProductRepository;
import com.eyecommer.Backend.service.OrderService;
import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import com.eyecommer.Backend.utils.SnapshotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderSnapshotRepository orderSnapshotRepository;
    private final VariantProductRepository variantProductRepository;
    private final OrderRepository orderRepository;
    private final com.eyecommer.Backend.repository.orderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Order confirmOrder(String orderCode) {
        OrderSnapshot snapshot = orderSnapshotRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Snapshot not found"));

        if (!PaymentStatus.UNPAID.name().equals(snapshot.getPaymentStatus())) {
            throw new RuntimeException("Payment status invalid");
        }

        Address address = addressRepository.findById(snapshot.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 1️⃣ TẠO ORDER
        Order order = new Order();

        order.setUser(snapshot.getUser());
        order.setAddress(address);
        order.setPaymentStatus(PaymentStatus.UNPAID.name());
        order.setStatus(OrderStatus.CONFIRMED.name());
        order.setTotalAmount(snapshot.getFinalAmount());

        orderRepository.save(order);

        // 2️⃣ TẠO ORDER ITEM + TRỪ KHO
        for (OrderItemSnapshot snapItem : snapshot.getItems()) {

            VariantProduct variant = variantProductRepository
                    .findByIdForUpdate(snapItem.getVariantId())
                    .orElseThrow();

            // Trừ kho thật
            variant.setStock(variant.getStock() - snapItem.getQuantity());
            variant.setReservedStock(
                    variant.getReservedStock() - snapItem.getQuantity()
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariantProduct(variant);
            orderItem.setPrice(snapItem.getPriceAtPurchase());
            orderItem.setQuantity(snapItem.getQuantity());

            orderItemRepository.save(orderItem);
        }

        // 3️⃣ ĐÁNH DẤU SNAPSHOT ĐÃ DÙNG
//        snapshot.setSnapshotStatus(SnapshotStatus.CONVERTED.name());
        orderSnapshotRepository.save(snapshot);

        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(String orderCode) {

        OrderSnapshot snapshot = orderSnapshotRepository
                .findByOrderCode(orderCode)
                .orElseThrow();

        if (!PaymentStatus.UNPAID.name().equals(snapshot.getPaymentStatus())) {
            return;
        }

        for (OrderItemSnapshot item : snapshot.getItems()) {

            VariantProduct variant = variantProductRepository
                    .findByIdForUpdate(item.getVariantId())
                    .orElseThrow();

            variant.setReservedStock(
                    variant.getReservedStock() - item.getQuantity()
            );
        }

        snapshot.setPaymentStatus(PaymentStatus.FAILED.name());
    }

}
