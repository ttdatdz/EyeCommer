package com.eyecommer.Backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoucherApplyResponse {
    private String code;
    private Double discountAmount;
    private Double finalAmount;
}
