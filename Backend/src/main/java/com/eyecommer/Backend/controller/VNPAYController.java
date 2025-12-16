package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.service.OrderSnapshotService;
import com.eyecommer.Backend.service.PaymentService;
import com.eyecommer.Backend.service.VNPAYService;
import com.eyecommer.Backend.service.impl.OrderSnapshotServiceImpl;
import com.eyecommer.Backend.utils.SnapshotCancelReason;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
public class VNPAYController {

    private final VNPAYService vnpayService;
    private final PaymentService paymentService;

//    private static final String FRONTEND_SUCCESS_URL =
//            "http://localhost:3000/payment-success";

    private static final String FRONTEND_SUCCESS_URL =
            "https://english-vocabulary-system.vercel.app/VnpayResult";
    private static final String FRONTEND_FAIL_URL =
            "http://localhost:3000/payment-fail";
    private final OrderSnapshotService orderSnapshotService;

    @GetMapping("/return")
    public void vnpayReturn(
            @RequestParam Map<String, String> params,
            HttpServletResponse response
    ) throws IOException {

        // 1️⃣ Verify signature
        boolean isValid = vnpayService.verifyCallback(params);

        String orderCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode"); // "00" = success

        if (!isValid) {
            paymentService.handlePaymentFail(orderCode);
            response.sendRedirect(
                    FRONTEND_FAIL_URL + "?message=Invalid signature"
            );
            return;
        }

        // 2️⃣ Xử lý kết quả thanh toán
        if ("00".equals(responseCode)) {

            paymentService.handlePaymentSuccess(orderCode);
            orderSnapshotService.confirmSnapshot(orderCode);
            response.sendRedirect(
                    FRONTEND_SUCCESS_URL + "?orderCode=" + orderCode
            );

        } else {

            paymentService.handlePaymentFail(orderCode);
            orderSnapshotService.cancelSnapshot(orderCode, SnapshotCancelReason.PAYMENT_FAILED);
            response.sendRedirect(
                    FRONTEND_FAIL_URL + "?orderCode=" + orderCode
            );
        }
    }
}
