package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.VariantProductRequestDTO;
import com.eyecommer.Backend.dto.response.VariantProductResponseDTO;
import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.stereotype.Component;

@Component
public class VariantProductMapper {

    /**
     * Chuyển đổi VariantProductRequest sang VariantProduct Entity.
     * Lưu ý: Không gán Product ở đây, logic này thuộc về Service.
     */
    public VariantProduct toEntity(VariantProductRequestDTO dto) {
        if (dto == null) return null;

        VariantProduct variant = new VariantProduct();

        // --- Thuộc tính Variant ---
        variant.setSku(dto.getSku());
        variant.setPrice(dto.getPrice());
        variant.setStock(dto.getStock());

        // Bỏ qua các trường quan hệ (product, images, attributes, orderItems, stockReceiptItems)
        // vì chúng sẽ được quản lý bởi Service Layer khi tạo.

        return variant;
    }
    public VariantProductResponseDTO toDTO(VariantProduct entity) {
        if (entity == null) return null;

        VariantProductResponseDTO dto = new VariantProductResponseDTO();
        dto.setId(entity.getId());
        dto.setSku(entity.getSku());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());

        // Thêm ánh xạ cho Images, Attributes nếu có DTO tương ứng

        return dto;
    }
}