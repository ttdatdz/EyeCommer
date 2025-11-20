package com.eyecommer.Backend.dto.request;

import lombok.Getter;

@Getter
public class AddressRequestDTO {
    private String addressDetail;

    private String city;

    private String district;

    private String postalCode;
}
