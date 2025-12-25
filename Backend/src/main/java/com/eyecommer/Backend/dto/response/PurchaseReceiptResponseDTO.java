package com.eyecommer.Backend.dto.response;

import com.eyecommer.Backend.utils.PurchaseReceiptStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseReceiptResponseDTO {

    private Long id;

    private String supplierName;
    private String supplierEmail;
    private String supplierPhone;

    private PurchaseReceiptStatus status;

    private Double totalAmount;

    private LocalDateTime receiptDate;


    private List<PurchaseReceiptProductResponseDTO> products;
}
