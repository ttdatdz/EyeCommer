package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.dto.response.SupplierResponseDTO;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.Supplier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplierMapper {

    public SupplierResponseDTO toDTO(Supplier supplier) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress(supplier.getAddress());
        dto.setTotalPurchaseReceipts(
                supplier.getStockReceipts() == null ? 0L :
                        (long) supplier.getStockReceipts().size()
        );
        return dto;
    }

    public List<SupplierResponseDTO> toDTOList(List<Supplier> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}