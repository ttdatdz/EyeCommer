package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.CheckoutRequestDTO;
import com.eyecommer.Backend.dto.response.CheckoutResponseDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.service.CheckoutService;
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

    @PostMapping
    public ResponseData<?> checkout(@RequestBody CheckoutRequestDTO request) {
        try {
            CheckoutResponseDTO response = checkoutService.checkout(request);

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

