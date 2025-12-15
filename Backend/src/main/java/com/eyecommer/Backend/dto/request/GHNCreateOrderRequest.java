package com.eyecommer.Backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GHNCreateOrderRequest {
    private String to_name;
    private String to_phone;
    private String to_address;
    private Integer to_district_id;
    private String to_ward_code;

    private Integer service_type_id;
    private Integer payment_type_id;

    private List<GHNItemDTO> items;
}
