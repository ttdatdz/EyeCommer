package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.CheckoutRequestDTO;
import com.eyecommer.Backend.dto.response.CheckoutResponseDTO;

public interface CheckoutService {
    CheckoutResponseDTO checkout(CheckoutRequestDTO request);
}