package com.eyecommer.Backend.service;

import com.eyecommer.Backend.model.OrderSnapshot;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface VNPAYService {

    String createPayment(OrderSnapshot snapshot, String clientIp);

    boolean verifyCallback(Map<String, String> params);


    String getClientIp(HttpServletRequest httpRequest);
}