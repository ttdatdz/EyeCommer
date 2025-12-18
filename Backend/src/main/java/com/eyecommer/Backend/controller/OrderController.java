package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.CancelOrderRequestDTO;
import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.OrderDetailResponseDTO;
import com.eyecommer.Backend.dto.response.OrderSummaryResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;

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

    // ===================== GET my orders =====================
    @GetMapping("/my")
    public ResponseData<?> getMyOrders(
            Principal principal,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search
    ) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            PageResponse<?> pageResponse = orderService.getMyOrders(user.getId(), pageNo, pageSize, sortBy, search);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách đơn hàng của tôi thành công", pageResponse);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Lấy đơn hàng thất bại: " + e.getMessage());
        }
    }

    // ===================== GET all orders (admin) =====================
    @GetMapping
    public ResponseData<?> getAllOrders(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search
    ) {
        try {
            PageResponse<?> pageResponse = orderService.getAllOrders(pageNo, pageSize, sortBy, search);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách tất cả đơn hàng thành công", pageResponse);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy danh sách đơn hàng thất bại: " + e.getMessage());
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
            orderService.cancelOrder(
                    request.getOrderCode(),
                    request.getReason()
            );

            return new ResponseData<>(
                    200,
                    "Hủy đơn hàng thành công"
            );
        } catch (Exception e) {
            return new ResponseData<>(
                    400,
                    e.getMessage()
            );
        }
    }
}
