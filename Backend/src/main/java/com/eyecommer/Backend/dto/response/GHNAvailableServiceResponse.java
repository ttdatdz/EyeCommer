package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class GHNAvailableServiceResponse {

    private Integer service_id;
    private String short_name;
    private Integer service_type_id;
}
