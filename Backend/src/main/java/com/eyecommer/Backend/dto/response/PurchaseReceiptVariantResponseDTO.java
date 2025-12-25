package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class PurchaseReceiptVariantResponseDTO {

    private Long variantId;
    private String sku;

    private Integer quantity;
    private Double price;

    private Integer stockAfterReceived; //Số lượng nhập chính thức
    // chỉ có giá trị khi status = COMPLETED
}
