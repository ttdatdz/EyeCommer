package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.model.OrderSnapshot;
import com.eyecommer.Backend.repository.OrderSnapshotRepository;
import com.eyecommer.Backend.service.VNPAYService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VNPAYController {

    private final VNPAYService vnpayService;
    private final OrderSnapshotRepository orderSnapshotRepository;

    // Frontend URL redirect sau khi thanh toán
    private final String FRONTEND_SUCCESS_URL = "http://localhost:3000/payment-success";
    private final String FRONTEND_FAIL_URL = "http://localhost:3000/payment-fail";

    @GetMapping("/vnpay/return")
    public void vnpayReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        // 1️⃣ Verify callback
        boolean isValid = vnpayService.verifyCallback(params);

        // 2️⃣ Lấy orderCode từ params
        String orderCode = params.get("vnp_TxnRef");

        OrderSnapshot snapshot = orderSnapshotRepository.findByOrderCode(orderCode)
                .orElse(null);

        if (snapshot == null) {
            // Không tìm thấy order
            response.sendRedirect(FRONTEND_FAIL_URL + "?message=Order not found");
            return;
        }

        if (!isValid) {
            snapshot.setPaymentStatus("FAILED");
            orderSnapshotRepository.save(snapshot);
            response.sendRedirect(FRONTEND_FAIL_URL + "?message=Invalid signature");
            return;
        }

        // 3️⃣ Kiểm tra response code từ VNPAY
        String vnpResponseCode = params.get("vnp_ResponseCode"); // 00 = success
        if ("00".equals(vnpResponseCode)) {
            snapshot.setPaymentStatus("PAID");
            orderSnapshotRepository.save(snapshot);
            response.sendRedirect(FRONTEND_SUCCESS_URL + "?orderCode=" + snapshot.getOrderCode());
        } else {
            snapshot.setPaymentStatus("FAILED");
            orderSnapshotRepository.save(snapshot);
            response.sendRedirect(FRONTEND_FAIL_URL + "?orderCode=" + snapshot.getOrderCode());
        }
    }
}
