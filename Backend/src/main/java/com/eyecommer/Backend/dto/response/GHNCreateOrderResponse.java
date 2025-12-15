package com.eyecommer.Backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHNCreateOrderResponse {
    private Data data;

    @lombok.Data
    public static class Data {
        private String order_code;
        private Long total_fee;
        private String expected_delivery_time;
    }
}
