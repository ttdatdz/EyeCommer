package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class AddressResponseDTO {
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String addressDetail;
    private String city;
    private String district;
    private String postalCode;
    private Boolean isDefault;

    private String ward;
    private Integer districtId;
    private String wardCode;
}
