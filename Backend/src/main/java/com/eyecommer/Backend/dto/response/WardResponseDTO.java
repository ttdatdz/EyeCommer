package com.eyecommer.Backend.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WardResponseDTO {

    @JsonProperty("WardCode")
    private String wardCode;

    @JsonProperty("DistrictID")
    private Integer districtId;

    @JsonProperty("WardName")
    private String wardName;
}