package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.service.OrderSnapshotService;
import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.PaymentStatus;
import com.eyecommer.Backend.utils.SnapshotCancelReason;
import com.eyecommer.Backend.utils.SnapshotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderSnapshotServiceImpl implements OrderSnapshotService {

    private final OrderSnapshotRepository snapshotRepo;
    private final VariantProductRepository variantRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final AddressRepository addressRepo;

//    @Override
//    @Transactional
//    public void confirmSnapshot(String orderCode) {
//
//        OrderSnapshot snapshot = snapshotRepo
//                .findByOrderCode(orderCode)
//                .orElseThrow(() -> new RuntimeException("Snapshot not found"));
//
//        if (snapshot.getStatus() != SnapshotStatus.PENDING) {
//            throw new RuntimeException("Snapshot already processed");
//        }
//
//        if (!(snapshot.getPaymentStatus() == PaymentStatus.UNPAID
//                || snapshot.getPaymentStatus() == PaymentStatus.PAID)) {
//            throw new RuntimeException("Snapshot not eligible");
//        }
//
//        Address address = addressRepo.findById(snapshot.getAddressId())
//                .orElseThrow(() -> new RuntimeException("Address not found"));
//
//        // 1️⃣ Create Order
//        Order order = new Order();
//        order.setOrderCode(snapshot.getOrderCode());
//        order.setUser(snapshot.getUser());
//        order.setAddress(address);
//        order.setStatus(OrderStatus.PENDING);
//        order.setPaymentStatus(snapshot.getPaymentStatus());
//        order.setTotalAmount(snapshot.getFinalAmount());
//
//        orderRepo.save(order);
//
//        // 2️⃣ Convert items + stock
//        for (OrderItemSnapshot item : snapshot.getItems()) {
//
//            VariantProduct variant = variantRepo
//                    .findByIdForUpdate(item.getVariantId())
//                    .orElseThrow();
//
//            variant.setStock(variant.getStock() - item.getQuantity());
//            variant.setReservedStock(variant.getReservedStock() - item.getQuantity());
//
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setVariantProduct(variant);
//            orderItem.setPrice(item.getPriceAtPurchase());
//            orderItem.setQuantity(item.getQuantity());
//
//            orderItemRepo.save(orderItem);
//        }
//
//        // 3️⃣ Mark snapshot converted
//        snapshot.setStatus(SnapshotStatus.CONVERTED);
//    }
//
//    @Override
//    @Transactional
//    public void cancelSnapshot(String orderCode, SnapshotCancelReason reason) {
//
//        OrderSnapshot snapshot = snapshotRepo
//                .findByOrderCode(orderCode)
//                .orElseThrow();
//
//        if (snapshot.getStatus() != SnapshotStatus.PENDING) {
//            return;
//        }
//
//        for (OrderItemSnapshot item : snapshot.getItems()) {
//            VariantProduct variant = variantRepo
//                    .findByIdForUpdate(item.getVariantId())
//                    .orElseThrow();
//
//            variant.setReservedStock(
//                    variant.getReservedStock() - item.getQuantity()
//            );
//        }
//
//        snapshot.setStatus(SnapshotStatus.CANCELLED);
//        snapshot.setCancelReason(reason);
//    }
    @Override
    @Transactional
    public void confirmSnapshot(String orderCode) {

        OrderSnapshot snapshot = snapshotRepo
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Snapshot not found"));

        if (snapshot.getStatus() != SnapshotStatus.PENDING) {
            throw new RuntimeException("Snapshot already processed");
        }

        if (snapshot.getPaymentStatus() != PaymentStatus.PAID
                && snapshot.getPaymentStatus() != PaymentStatus.UNPAID) {
            throw new RuntimeException("Snapshot not eligible");
        }

        Address address = addressRepo.findById(snapshot.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 1️⃣ Create Order
        Order order = new Order();
        order.setOrderCode(snapshot.getOrderCode());
        order.setUser(snapshot.getUser());
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(snapshot.getPaymentStatus());
        order.setTotalAmount(snapshot.getFinalAmount());

        orderRepo.save(order);

        // 2️⃣ Trừ kho thật + giải phóng reserved
        for (OrderItemSnapshot item : snapshot.getItems()) {

            VariantProduct variant = variantRepo
                    .findByIdForUpdate(item.getVariantId())
                    .orElseThrow();

            variant.setStock(variant.getStock() - item.getQuantity());
            variant.setReservedStock(variant.getReservedStock() - item.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariantProduct(variant);
            orderItem.setPrice(item.getPriceAtPurchase());
            orderItem.setQuantity(item.getQuantity());

            orderItemRepo.save(orderItem);
        }

        snapshot.setStatus(SnapshotStatus.CONVERTED);
    }
    @Override
    @Transactional
    public void cancelSnapshot(String orderCode, SnapshotCancelReason reason) {

        OrderSnapshot snapshot = snapshotRepo
                .findByOrderCode(orderCode)
                .orElseThrow();

        if (snapshot.getStatus() != SnapshotStatus.PENDING) {
            return;
        }

        for (OrderItemSnapshot item : snapshot.getItems()) {

            VariantProduct variant = variantRepo
                    .findByIdForUpdate(item.getVariantId())
                    .orElseThrow();

            variant.setReservedStock(
                    variant.getReservedStock() - item.getQuantity()
            );
        }

        snapshot.setStatus(SnapshotStatus.CANCELLED);
        snapshot.setCancelReason(reason);
    }

}

