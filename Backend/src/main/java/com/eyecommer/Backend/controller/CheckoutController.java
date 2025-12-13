package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.CheckoutRequestDTO;
import com.eyecommer.Backend.dto.response.CheckoutResponseDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.CheckoutService;
import com.eyecommer.Backend.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final VNPAYService vnpayService;
    @PostMapping
    public ResponseData<?> checkout(@RequestBody CheckoutRequestDTO request, HttpServletRequest httpRequest) {
        try {
            String clientIp = vnpayService.getClientIp(httpRequest);
            CheckoutResponseDTO response = checkoutService.checkout(request,clientIp);

            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Checkout success",
                    response
            );

        } catch (Exception e) {
            return new ResponseData<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Checkout failed because: " + e.getMessage(),
                    null
            );
        }
    }
}

