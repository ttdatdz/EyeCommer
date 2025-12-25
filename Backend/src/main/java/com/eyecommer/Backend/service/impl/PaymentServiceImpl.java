package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.CartItemRepository;
import com.eyecommer.Backend.repository.CartRepository;
import com.eyecommer.Backend.repository.OrderSnapshotRepository;
import com.eyecommer.Backend.repository.VariantProductRepository;
import com.eyecommer.Backend.service.OrderSnapshotService;
import com.eyecommer.Backend.service.PaymentService;
import com.eyecommer.Backend.utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderSnapshotRepository orderSnapshotRepository;
    private final VariantProductRepository variantProductRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderSnapshotService orderSnapshotService;



    @Override
    @Transactional
    public void handlePaymentSuccess(String orderCode) {

        OrderSnapshot snapshot = orderSnapshotRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Snapshot not found"));

        if (snapshot.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        snapshot.setPaymentStatus(PaymentStatus.PAID);
        //  1. TRỪ CART DỰA TRÊN SNAPSHOT
        Set<OrderItemSnapshot> snapItems = snapshot.getItems();

        for (OrderItemSnapshot snapItem : snapItems) {

            // tìm cart item tương ứng
            CartItem cartItem = cartItemRepository
                    .findByCart_User_IdAndVariantProduct_Id(
                            snapshot.getUser().getId(),
                            snapItem.getVariantId()
                    )
                    .orElse(null);

            if (cartItem == null) continue; // có thể user đã xóa cart sau đó

            int checkoutQty = snapItem.getQuantity();

            if (checkoutQty == cartItem.getQuantity()) {
                cartItemRepository.delete(cartItem);
            } else if (checkoutQty < cartItem.getQuantity()) {
                cartItem.setQuantity(cartItem.getQuantity() - checkoutQty);
                cartItemRepository.save(cartItem);
            } else {
                throw new RuntimeException("Checkout quantity exceeds cart quantity");
            }
        }

        //  2. CONFIRM SNAPSHOT (chuyển reserved → stock thật)
        orderSnapshotService.confirmSnapshot(snapshot.getOrderCode());
    }
    @Override
    @Transactional
    public void handlePaymentFail(String orderCode) {

        OrderSnapshot snapshot = orderSnapshotRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Snapshot not found"));

        if (snapshot.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }

        snapshot.setPaymentStatus(PaymentStatus.FAILED);
    }

}
