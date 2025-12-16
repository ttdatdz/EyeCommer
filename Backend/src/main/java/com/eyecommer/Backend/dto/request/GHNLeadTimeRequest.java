package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class GHNLeadTimeRequest {

    private Integer from_district_id;
    private String from_ward_code;

    private Integer to_district_id;
    private String to_ward_code;

    private Integer service_id; // Mã dịch vụ GHN (standard / express).lấy từ API service của GHN
}