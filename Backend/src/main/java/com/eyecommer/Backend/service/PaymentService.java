package com.eyecommer.Backend.service;

public interface PaymentService {
    void handlePaymentSuccess(String orderCode);

    void handlePaymentFail(String orderCode);
}
