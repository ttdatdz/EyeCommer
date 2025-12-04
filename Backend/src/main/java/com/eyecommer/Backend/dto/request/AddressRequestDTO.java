package com.eyecommer.Backend.dto.request;

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
    @NotNull
    private String postalCode;
    @NotNull
    private Boolean isDefault;
}
