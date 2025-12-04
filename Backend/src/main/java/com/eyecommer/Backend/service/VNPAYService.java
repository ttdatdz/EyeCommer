package com.eyecommer.Backend.service;

import com.eyecommer.Backend.model.OrderSnapshot;

import java.util.Map;

public interface VNPAYService {

    String createPayment(OrderSnapshot snapshot);

    boolean verifyCallback(Map<String, String> params);
}