package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.response.PurchaseReceiptItemResponseDTO;
import com.eyecommer.Backend.dto.response.PurchaseReceiptResponseDTO;
import com.eyecommer.Backend.model.StockReceipts;
import org.springframework.stereotype.Component;

@Component
public class PurchaseReceiptMapper {

    public PurchaseReceiptResponseDTO toDTO(StockReceipts receipt) {
        PurchaseReceiptResponseDTO dto = new PurchaseReceiptResponseDTO();
        dto.setId(receipt.getId());
        dto.setSupplierName(receipt.getSupplier().getName());
        dto.setStatus(receipt.getStatus());
        dto.setReceiptDate(receipt.getReceiptDate());
        dto.setTotalAmount(receipt.getTotalAmount());

        dto.setItems(
                receipt.getItems().stream().map(item -> {
                    PurchaseReceiptItemResponseDTO i = new PurchaseReceiptItemResponseDTO();
                    i.setSku(item.getVariantProduct().getSku());
                    i.setProductName(item.getVariantProduct().getProduct().getName());
                    i.setQuantity(item.getQuantity());
                    i.setPrice(item.getPrice());
                    return i;
                }).toList()
        );

        return dto;
    }
}