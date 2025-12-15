package com.eyecommer.Backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DistrictResponseDTO {

    @JsonProperty("DistrictID")
    private Integer districtId;

    @JsonProperty("ProvinceID")
    private Integer provinceId;

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Type")
    private Integer type;

    @JsonProperty("SupportType")
    private Integer supportType;
}
