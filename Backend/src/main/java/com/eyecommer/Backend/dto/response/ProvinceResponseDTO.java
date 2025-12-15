package com.eyecommer.Backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProvinceResponseDTO {
    @JsonProperty("ProvinceID")
    private Integer provinceID;

    @JsonProperty("ProvinceName")
    private String provinceName;
}

