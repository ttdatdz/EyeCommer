package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.model.OrderSnapshot;
import com.eyecommer.Backend.service.VNPAYService;
import com.eyecommer.Backend.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VNPAYServiceImpl implements VNPAYService {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String secretKey;

    @Value("${vnpay.url}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @Override
    public String createPayment(OrderSnapshot snapshot, String clientIp) {
        try {
            Map<String, String> vnpParams = new HashMap<>();

            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnp_TmnCode);
            vnpParams.put("vnp_Amount", String.valueOf((long) (snapshot.getFinalAmount() * 100)));
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", snapshot.getOrderCode());
            vnpParams.put("vnp_OrderInfo", VNPayUtil.removeVietnameseAccent("Payment for order: " + snapshot.getOrderCode()));
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnpParams.put("vnp_IpAddr", clientIp);

            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            vnpParams.put("vnp_CreateDate", now.format(formatter));
            vnpParams.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));

            // Sinh URL VNPAY
            return VNPayUtil.getPaymentUrlLikeVnPaySample(vnpParams, secretKey, vnp_PayUrl);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error creating VNPAY payment URL", e);
        }
    }


//   Hàm này để kiểm tra xem callback có đúng do VNPAY tạo hay không. bằng cách tạo lại chữ ký từ các tham số
    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            // Lấy chữ ký gửi từ VNPAY
            String vnp_SecureHash = params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            // Tạo lại chữ ký từ dữ liệu nhận được
            String calculatedHash = VNPayUtil.hashAllFields(params, secretKey);

            return calculatedHash.equalsIgnoreCase(vnp_SecureHash);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error verifying VNPAY callback", e);
        }
    }

    @Override
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Nếu có nhiều IP (trường hợp dùng proxy), lấy IP đầu tiên
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
