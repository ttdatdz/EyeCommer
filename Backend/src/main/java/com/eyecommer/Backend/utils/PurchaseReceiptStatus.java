package com.eyecommer.Backend.utils;

public enum PurchaseReceiptStatus {
    PENDING,    // Đã tạo, chờ hàng về
    COMPLETED, // Đã nhập kho
    RETURNED   // Trả hàng / lỗi toàn bộ
}
