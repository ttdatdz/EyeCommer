package com.eyecommer.Backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDTO {
    private Long id;
    private String addressDetail;
    private String city;
    private String district;
    private String postalCode;
}
