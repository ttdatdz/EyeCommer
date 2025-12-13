package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.CartItemRepository;
import com.eyecommer.Backend.repository.CartRepository;
import com.eyecommer.Backend.repository.OrderSnapshotRepository;
import com.eyecommer.Backend.repository.VariantProductRepository;
import com.eyecommer.Backend.service.PaymentService;
import com.eyecommer.Backend.utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderSnapshotRepository orderSnapshotRepository;
    private final VariantProductRepository variantProductRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public void handlePaymentSuccess(String orderCode) {

        OrderSnapshot snapshot = orderSnapshotRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Cart cart = cartRepository
                .findByUserId(snapshot.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        for (OrderItemSnapshot item : snapshot.getItems()) {

            // 1 LOCK VARIANT
            VariantProduct variant = variantProductRepository
                    .findByIdForUpdate(item.getVariantId())
                    .orElseThrow();

            // 2 TRỪ KHO THẬT
            variant.setStock(variant.getStock() - item.getQuantity());

            // 3 GIẢI PHÓNG RESERVED
            variant.setReservedStock(
                    variant.getReservedStock() - item.getQuantity()
            );

            // 4 UPDATE CART ITEM ĐÚNG LOGIC
            CartItem cartItem = cartItemRepository
                    .findByCartIdAndVariantProductId(cart.getId(), variant.getId())
                    .orElse(null);

            if (cartItem == null) {
                continue; // item này không còn trong cart → skip
            }

            int checkoutQty = item.getQuantity();
            int cartQty = cartItem.getQuantity();

            if (checkoutQty == cartQty) {
                // xoá
                cartItemRepository.delete(cartItem);

            } else if (checkoutQty < cartQty) {
                // giảm
                cartItem.setQuantity(cartQty - checkoutQty);
                cartItemRepository.save(cartItem);

            } else {
                // không thể xảy ra nếu checkout validate đúng
                throw new RuntimeException("Checkout quantity exceeds cart quantity");
            }
        }

        snapshot.setPaymentStatus(PaymentStatus.PAID.name());
        orderSnapshotRepository.save(snapshot);
    }

    @Override
    @Transactional
    public void handlePaymentFail(String orderCode) {

        OrderSnapshot snapshot = orderSnapshotRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!PaymentStatus.UNPAID.name().equals(snapshot.getPaymentStatus())) {
            return; // chỉ trả kho nếu chưa trả
        }

        for (OrderItemSnapshot item : snapshot.getItems()) {

            VariantProduct variant = variantProductRepository
                    .findByIdForUpdate(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            variant.setReservedStock(
                    variant.getReservedStock() - item.getQuantity()
            );
        }

        snapshot.setPaymentStatus(PaymentStatus.FAILED.name());
    }
}
