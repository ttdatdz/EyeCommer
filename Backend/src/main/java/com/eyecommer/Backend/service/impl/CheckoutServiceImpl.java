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
import com.eyecommer.Backend.utils.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final VariantProductRepository variantProductRepository;

    @Override
    @Transactional
    public CheckoutResponseDTO checkout(CheckoutRequestDTO request, String clientIp) {

        // 1. Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Validate address
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Address does not belong to user");
        }

        // 3. Validate items
        List<CartItemCheckoutDTO> reqItems = request.getItems();
        if (reqItems == null || reqItems.isEmpty()) {
            throw new RuntimeException("No items selected");
        }

        Map<Long, Integer> qtyMap = reqItems.stream()
                .collect(Collectors.toMap(
                        CartItemCheckoutDTO::getCartItemId,
                        CartItemCheckoutDTO::getQuantity
                ));

        // 4. Lấy cart items của đúng user
        List<CartItem> cartItems =
                cartItemRepository.findByIdInAndCart_User_Id(qtyMap.keySet(), user.getId());

        if (cartItems.size() != qtyMap.size()) {
            throw new RuntimeException("Some cart items are invalid");
        }

        // 5. Create snapshot
        OrderSnapshot snapshot = new OrderSnapshot();
        snapshot.setOrderCode(UUID.randomUUID().toString());
        snapshot.setUser(user);
        snapshot.setAddressId(address.getId());
        snapshot.setAddressDetail(address.getAddressDetail());
        snapshot.setPaymentMethod(request.getPaymentMethod());
        snapshot.setPaymentStatus(PaymentStatus.UNPAID.name());

        double totalAmount = 0;

        // 6. LOCK + RESERVE STOCK
        for (CartItem item : cartItems) {

            Integer qty = qtyMap.get(item.getId());
            if (qty == null || qty <= 0 || qty > item.getQuantity()) {
                throw new RuntimeException("Invalid quantity for cartItemId: " + item.getId());
            }

            VariantProduct variant = variantProductRepository
                    .findByIdForUpdate(item.getVariantProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            int reserved = variant.getReservedStock() == null ? 0 : variant.getReservedStock();
            int available = variant.getStock() - reserved;

            if (available < qty) {
                throw new RuntimeException(
                        "Not enough stock for product: " + variant.getProduct().getName()
                );
            }

            //  CHỈ GIỮ Hàng
            variant.setReservedStock(reserved + qty);

            // Snapshot item
            OrderItemSnapshot snapItem = new OrderItemSnapshot();
            snapItem.setProductId(variant.getProduct().getId());
            snapItem.setProductName(variant.getProduct().getName());
            snapItem.setVariantId(variant.getId());
            snapItem.setVariantName(variant.getSku());

            snapItem.setImageUrl(
                    variant.getImages() != null && !variant.getImages().isEmpty()
                            ? variant.getImages().stream()
                            .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                            .findFirst()
                            .orElse(variant.getImages().iterator().next())
                            .getImageUrl()
                            : null
            );

            snapItem.setPriceAtPurchase(variant.getPrice());
            snapItem.setQuantity(qty);
            snapItem.setLineTotal(variant.getPrice() * qty);

            snapshot.addItem(snapItem);
            totalAmount += snapItem.getLineTotal();
        }

        snapshot.setTotalAmount(totalAmount);

        // 7. Voucher
        double finalAmount = totalAmount;
        if (request.getVoucherId() != null) {
            var applied = voucherService.applyVoucher(request.getVoucherId(), totalAmount);
            snapshot.setVoucherId(request.getVoucherId());
            snapshot.setVoucherCode(applied.getCode());
            snapshot.setVoucherDiscountAmount(applied.getDiscountAmount());
            finalAmount = applied.getFinalAmount();
        }

        snapshot.setFinalAmount(finalAmount);

        // 8. Save snapshot
        orderSnapshotRepository.save(snapshot);

        // 9. Payment
        if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
            // COD: coi như chấp nhận giữ kho
            updateCartAfterCheckout(cartItems, qtyMap);
            return orderSnapshotMapper.toCheckoutResponseDTO(snapshot, null);
        }

        if ("VNPAY".equalsIgnoreCase(request.getPaymentMethod())) {
            String payUrl = vnpayService.createPayment(snapshot,clientIp);
            return orderSnapshotMapper.toCheckoutResponseDTO(snapshot, payUrl);
        }

        throw new RuntimeException("Invalid payment method");
    }


    private void updateCartAfterCheckout(
            List<CartItem> cartItems,
            Map<Long, Integer> qtyMap
    ) {
        for (CartItem item : cartItems) {

            Integer checkoutQty = qtyMap.get(item.getId());

            if (checkoutQty == null) continue;

            if (checkoutQty.equals(item.getQuantity())) {
                cartItemRepository.delete(item);
            } else if (checkoutQty < item.getQuantity()) {
                item.setQuantity(item.getQuantity() - checkoutQty);
                cartItemRepository.save(item);
            } else {
                throw new RuntimeException("Checkout quantity exceeds cart quantity");
            }
        }
    }


}
