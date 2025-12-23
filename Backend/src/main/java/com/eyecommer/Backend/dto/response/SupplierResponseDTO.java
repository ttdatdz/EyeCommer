package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class SupplierResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;

    private Long totalPurchaseReceipts;
}
