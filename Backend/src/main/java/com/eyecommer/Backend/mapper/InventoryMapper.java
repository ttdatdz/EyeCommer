package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.response.InventoryResponseDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.VariantProduct;
import com.eyecommer.Backend.utils.InventoryStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {

    public InventoryResponseDTO toDTO(VariantProduct variant) {

        InventoryResponseDTO dto = new InventoryResponseDTO();

        dto.setVariantId(variant.getId());
        dto.setSku(variant.getSku());
        dto.setProductName(variant.getProduct().getName());

        Integer stock = variant.getStock() != null ? variant.getStock() : 0;
        Integer reserved = variant.getReservedStock() != null ? variant.getReservedStock() : 0;

        dto.setStock(stock);
        dto.setReservedStock(reserved);

        int available = stock - reserved;
        dto.setAvailableStock(available);

        dto.setPrice(variant.getPrice());

        if (available <= 0) {
            dto.setStatus(InventoryStatus.OUT_OF_STOCK);
        } else if (available <= 5) {
            dto.setStatus(InventoryStatus.LOW_STOCK);
        } else {
            dto.setStatus(InventoryStatus.IN_STOCK);
        }

        return dto;
    }
    public List<InventoryResponseDTO> toDTOList(List<VariantProduct> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
