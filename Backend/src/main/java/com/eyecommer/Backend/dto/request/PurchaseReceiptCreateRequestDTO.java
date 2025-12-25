package com.eyecommer.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseReceiptCreateRequestDTO {

    @NotBlank
    private Long supplierId;

    @NotEmpty
    private List<PurchaseReceiptProductDTO> products;
}
