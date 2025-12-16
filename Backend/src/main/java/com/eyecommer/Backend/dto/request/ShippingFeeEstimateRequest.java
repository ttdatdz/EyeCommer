package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class ShippingFeeEstimateRequest {

    private Integer from_district_id;
    private String from_ward_code;

    private Integer to_district_id;
    private String to_ward_code;

    private Integer service_id;

    private Integer weight;   // tổng hoặc ước tính
    private Integer length;
    private Integer width;
    private Integer height;
}

