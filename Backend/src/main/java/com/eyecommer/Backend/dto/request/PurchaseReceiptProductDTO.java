package com.eyecommer.Backend.dto.request;


import lombok.Data;

import java.util.List;

@Data
public class PurchaseReceiptProductDTO {

    private Long productId;          // null nếu sản phẩm mới
    private String productName;

    private List<PurchaseReceiptVariantDTO> variants;
}

