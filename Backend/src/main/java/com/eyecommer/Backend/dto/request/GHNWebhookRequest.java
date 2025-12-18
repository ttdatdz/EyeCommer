package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class GHNWebhookRequest {

    private String order_code; // Tracking number / order_code GHN
    private String status;     // Trạng thái GHN
    private Integer status_id; // Mã trạng thái GHN
    private String time;       // Timestamp
}