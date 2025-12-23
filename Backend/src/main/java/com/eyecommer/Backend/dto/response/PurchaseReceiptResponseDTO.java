package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.utils.PurchaseReceiptStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseReceiptResponseDTO {
    private Long id;
    private String supplierName;
    private PurchaseReceiptStatus status;
    private LocalDateTime receiptDate;
    private Double totalAmount;
    private List<PurchaseReceiptItemResponseDTO> items;
}
