package com.eyecommer.Backend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseReceiptProductResponseDTO {

    private Long productId;
    private String productName;

    private List<PurchaseReceiptVariantResponseDTO> variants;
}

