package com.eyecommer.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseReceiptCreateRequestDTO {

    // Supplier nhập text
    @NotBlank
    private String supplierName;

    // Optional – chỉ dùng khi tạo mới
    @NotBlank
    private String supplierEmail;
    @NotBlank
    private String supplierPhone;
    @NotBlank
    private String supplierAddress;

    @NotEmpty
    private List<PurchaseReceiptItemRequestDTO> items;
}
