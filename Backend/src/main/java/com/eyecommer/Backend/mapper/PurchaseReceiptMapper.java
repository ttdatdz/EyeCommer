package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.response.PurchaseReceiptItemResponseDTO;
import com.eyecommer.Backend.dto.response.PurchaseReceiptProductResponseDTO;
import com.eyecommer.Backend.dto.response.PurchaseReceiptResponseDTO;
import com.eyecommer.Backend.dto.response.PurchaseReceiptVariantResponseDTO;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.utils.PurchaseReceiptStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PurchaseReceiptMapper {
    public PurchaseReceiptResponseDTO toDTO(StockReceipts receipt) {
        PurchaseReceiptResponseDTO dto = new PurchaseReceiptResponseDTO();

        dto.setId(receipt.getId());
        dto.setStatus(receipt.getStatus());
        dto.setTotalAmount(receipt.getTotalAmount());
        dto.setReceiptDate(receipt.getReceiptDate());


        Supplier s = receipt.getSupplier();
        dto.setSupplierName(s.getName());
        dto.setSupplierEmail(s.getEmail());
        dto.setSupplierPhone(s.getPhone());

        dto.setProducts(mapProducts(receipt));

        return dto;
    }
    private List<PurchaseReceiptProductResponseDTO> mapProducts(StockReceipts receipt) {

        Map<Product, List<StockReceiptItem>> grouped =
                receipt.getItems().stream()
                        .collect(Collectors.groupingBy(
                                i -> i.getVariantProduct().getProduct()
                        ));

        List<PurchaseReceiptProductResponseDTO> products = new ArrayList<>();

        for (Map.Entry<Product, List<StockReceiptItem>> entry : grouped.entrySet()) {

            Product product = entry.getKey();
            List<StockReceiptItem> items = entry.getValue();

            PurchaseReceiptProductResponseDTO pDto =
                    new PurchaseReceiptProductResponseDTO();

            pDto.setProductId(product.getId());
            pDto.setProductName(product.getName());

            pDto.setVariants(mapVariants(items, receipt.getStatus()));

            products.add(pDto);
        }

        return products;
    }
    private List<PurchaseReceiptVariantResponseDTO> mapVariants(
            List<StockReceiptItem> items,
            PurchaseReceiptStatus status
    ) {
        List<PurchaseReceiptVariantResponseDTO> variants = new ArrayList<>();

        for (StockReceiptItem item : items) {

            VariantProduct v = item.getVariantProduct();

            PurchaseReceiptVariantResponseDTO dto =
                    new PurchaseReceiptVariantResponseDTO();

            dto.setVariantId(v.getId());
            dto.setSku(v.getSku());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());

            if (status == PurchaseReceiptStatus.COMPLETED) {
                dto.setStockAfterReceived(v.getStock());
            }

            variants.add(dto);
        }

        return variants;
    }
}