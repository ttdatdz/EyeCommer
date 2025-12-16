package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ===================== GET ORDER DETAIL =====================
    @GetMapping("/{orderCode}")
    public ResponseData<?> getOrderDetail(@PathVariable String orderCode) {
        try {
            OrderDetailResponseDTO order =
                    orderService.getOrderDetail(orderCode);

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Lấy chi tiết đơn hàng thành công",
                    order
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Lấy chi tiết đơn hàng thất bại: " + e.getMessage()
            );
        }
    }

    // ===================== GET MY ORDERS =====================
    @GetMapping("/my/{userId}")
    public ResponseData<?> getMyOrders(@PathVariable Long userId) {
        try {
            List<OrderSummaryResponseDTO> orders =
                    orderService.getMyOrders(userId);

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách đơn hàng của tôi thành công",
                    orders
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Lấy đơn hàng thất bại: " + e.getMessage()
            );
        }
    }

    // ===================== GET ALL ORDERS (ADMIN) =====================
    @GetMapping
    public ResponseData<?> getAllOrders() {
        try {
            List<OrderSummaryResponseDTO> orders =
                    orderService.getAllOrders();

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Lấy danh sách tất cả đơn hàng thành công",
                    orders
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Lấy danh sách đơn hàng thất bại: " + e.getMessage()
            );
        }
    }

    // ===================== CONFIRM ORDER (ADMIN / STAFF) =====================
    @PostMapping("/confirm")
    public ResponseData<?> confirmOrder(
            @RequestBody ConfirmOrderRequestDTO request
    ) {
        try {
            orderService.confirmOrder(request);

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Xác nhận đơn hàng thành công"
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Xác nhận đơn hàng thất bại: " + e.getMessage()
            );
        }
    }

    // ===================== CANCEL ORDER =====================
    @PostMapping("/cancel")
    public ResponseData<?> cancelOrder(
            @RequestBody CancelOrderRequestDTO request
    ) {
        try {
            orderService.cancelOrder(request);

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Hủy đơn hàng thành công"
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Hủy đơn hàng thất bại: " + e.getMessage()
            );
        }
    }
}
