package com.eyecommer.Backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderRequestDTO {
    private String orderCode;
    private String reason;
}
