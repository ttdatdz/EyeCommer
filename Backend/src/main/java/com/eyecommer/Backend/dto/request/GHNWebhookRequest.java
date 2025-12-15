package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class GHNWebhookRequest {

    private String order_code;
    private String status;
}