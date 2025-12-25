package com.eyecommer.Backend.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class PurchaseReceiptVariantDTO {

    // null → variant mới
    private Long variantId;

    // ===== CHỈ DÙNG CHO VARIANT MỚI =====
    private String sku;
    private Double sellingPrice; // GIÁ BÁN – set 1 lần duy nhất

    // ===== DÙNG CHUNG =====
    private Double purchasePrice; // GIÁ NHẬP (lưu trong ReceiptItem)
    private Integer quantity;
}
