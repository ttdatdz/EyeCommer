package com.eyecommer.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class AddressRequestDTO {
    @NotNull
    private String receiverName;
    @NotNull
    private String receiverPhone;
    @NotNull
    private String addressDetail;
    @NotNull
    private String city;
    @NotNull
    private String district;

    private String ward;

    @NotNull
    private String postalCode;
    @NotNull
    private Boolean isDefault;

    @NotNull
    private Integer districtId;

    @NotBlank
    private String wardCode;


}
