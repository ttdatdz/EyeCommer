package com.eyecommer.Backend.service.impl;


import com.eyecommer.Backend.dto.request.CartItemCheckoutDTO;
import com.eyecommer.Backend.dto.request.CheckoutRequestDTO;
import com.eyecommer.Backend.dto.response.CheckoutResponseDTO;
import com.eyecommer.Backend.mapper.OrderSnapshotMapper;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.*;
import com.eyecommer.Backend.service.CheckoutService;
import com.eyecommer.Backend.service.VNPAYService;
import com.eyecommer.Backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final VoucherService voucherService;
    private final OrderSnapshotRepository orderSnapshotRepository;
    private final VNPAYService vnpayService;
    private final OrderSnapshotMapper orderSnapshotMapper;

    @Override
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Lấy cart item được chọn
        List<CartItemCheckoutDTO> reqItems = request.getItems();
        if (reqItems.isEmpty()) throw new RuntimeException("No items selected");

        Map<Long, Integer> qtyMap = reqItems.stream()
                .collect(Collectors.toMap(CartItemCheckoutDTO::getCartItemId, CartItemCheckoutDTO::getQuantity));

        List<CartItem> cartItems = cartItemRepository.findAllById(qtyMap.keySet());

        OrderSnapshot snapshot = new OrderSnapshot();
        snapshot.setOrderCode(UUID.randomUUID().toString());
        snapshot.setUser(user);
        snapshot.setAddressId(address.getId());
        snapshot.setAddressDetail(address.getAddressDetail());
        snapshot.setPaymentMethod(request.getPaymentMethod());
        snapshot.setPaymentStatus("PENDING");

        double totalAmount = 0;

        for (CartItem item : cartItems) {
            Integer qty = qtyMap.get(item.getId());
            if (qty <= 0 || qty > item.getQuantity())
                throw new RuntimeException("Invalid quantity for cartItemId " + item.getId());

            VariantProduct variant = item.getVariantProduct();
            if (variant.getStock() < qty)
                throw new RuntimeException("Not enough stock for product: " + variant.getProduct().getName());

            OrderItemSnapshot snapItem = new OrderItemSnapshot();
            snapItem.setProductId(variant.getProduct().getId());
            snapItem.setProductName(variant.getProduct().getName());
            snapItem.setVariantId(variant.getId());
            snapItem.setVariantName(variant.getProduct().getName());
            snapItem.setImageUrl(
                    variant.getImages() != null && !variant.getImages().isEmpty()
                            ? variant.getImages().iterator().next().getImageUrl()
                            : null
            );
            snapItem.setPriceAtPurchase(variant.getPrice());
            snapItem.setQuantity(qty);
            snapItem.setLineTotal(variant.getPrice() * qty);

            snapshot.addItem(snapItem);
            totalAmount += snapItem.getLineTotal();
        }

        snapshot.setTotalAmount(totalAmount);

        double finalAmount = totalAmount;

        if (request.getVoucherId() != null) {
            var applied = voucherService.applyVoucher(request.getVoucherId(), totalAmount);
            snapshot.setVoucherId(request.getVoucherId());
            snapshot.setVoucherCode(applied.getCode());
            snapshot.setVoucherDiscountAmount(applied.getDiscountAmount());
            finalAmount = applied.getFinalAmount();
        }

        snapshot.setFinalAmount(finalAmount);

        // Lưu snapshot
        orderSnapshotRepository.save(snapshot);

        if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
            snapshot.setPaymentStatus("UNPAID");
            return orderSnapshotMapper.toCheckoutResponseDTO(snapshot, null);
        }

        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            String payUrl = vnpayService.createPayment(snapshot);
            return orderSnapshotMapper.toCheckoutResponseDTO(snapshot, payUrl);
        }

        throw new RuntimeException("Invalid payment method");
    }
}
