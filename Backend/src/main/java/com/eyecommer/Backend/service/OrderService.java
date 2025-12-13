package com.eyecommer.Backend.service;

import com.eyecommer.Backend.model.Order;

public interface OrderService {
    Order confirmOrder(String orderCode);
    void cancelOrder(String orderCode);
}
