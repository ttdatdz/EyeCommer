package com.eyecommer.Backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmOrderRequestDTO {
    private String orderCode;
    private Integer serviceTypeId; // 2 = tiêu chuẩn
    private Integer paymentTypeId; // 1: shop trả 2:người mua trả
}
