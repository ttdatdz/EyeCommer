package com.eyecommer.Backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GHNCreateOrderResponse {
    private Data data;

    @lombok.Data
    public static class Data {
        private String order_code;
        private Long total_fee;
        private LocalDateTime expected_delivery_time;
    }
}
