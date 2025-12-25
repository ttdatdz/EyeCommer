package com.eyecommer.Backend.dto.request;

import com.eyecommer.Backend.utils.PurchaseReceiptStatus;
import lombok.Data;

@Data
public class PurchaseReceiptUpdateRequestDTO {
    private Long receiptId;
    PurchaseReceiptStatus newStatus;
}
