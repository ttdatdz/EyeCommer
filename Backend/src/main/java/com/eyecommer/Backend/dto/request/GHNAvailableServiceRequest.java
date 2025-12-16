package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class GHNAvailableServiceRequest {

    private Integer shop_id;
    private Integer from_district;
    private Integer to_district;
}