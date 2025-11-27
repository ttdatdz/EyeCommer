package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.VariantProductRequestDTO;
import com.eyecommer.Backend.dto.request.VariantProductUpdateDTO;
import com.eyecommer.Backend.dto.response.AttributeResponseDTO;
import com.eyecommer.Backend.dto.response.VariantImageResponseDTO;
import com.eyecommer.Backend.dto.response.VariantProductResponseDTO;
import com.eyecommer.Backend.model.VariantImage;
import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VariantProductMapper {
    @Autowired
    private AttributeMapper attributeMapper; // Giả định có
    @Autowired
    private VariantImageMapper variantImageMapper;

    public VariantProduct toEntity(VariantProductRequestDTO dto) {
        if (dto == null) return null;

        VariantProduct variant = new VariantProduct();

        // --- Thuộc tính Variant ---
        variant.setSku(dto.getSku());
        variant.setPrice(dto.getPrice());
        variant.setStock(dto.getStock());

        // BỔ SUNG: Ánh xạ và gán VariantImage Entities
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            Set<VariantImage> images = dto.getImages().stream()
                    // Truyền VariantProduct vào hàm toEntity để thiết lập mối quan hệ ngược lại
                    .map(imgDto -> variantImageMapper.toEntity(imgDto, variant))
                    .collect(Collectors.toSet());

            variant.setImages(images);
        }

        return variant;
    }

    public VariantProduct toEntity(VariantProductUpdateDTO dto) {
        if (dto == null) return null;

        VariantProduct variant = new VariantProduct();

        // --- Thuộc tính Variant ---
        variant.setSku(dto.getSku());
        variant.setPrice(dto.getPrice());
        variant.setStock(dto.getStock());

        // BỔ SUNG: Ánh xạ và gán VariantImage Entities
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            Set<VariantImage> images = dto.getImages().stream()
                    // Truyền VariantProduct vào hàm toEntity để thiết lập mối quan hệ ngược lại
                    .map(imgDto -> variantImageMapper.toEntity(imgDto, variant))
                    .collect(Collectors.toSet());

            variant.setImages(images);
        }

        return variant;
    }
    public VariantProductResponseDTO toDTO(VariantProduct entity) {
        if (entity == null) return null;

        VariantProductResponseDTO dto = new VariantProductResponseDTO();
        dto.setId(entity.getId());
        dto.setSku(entity.getSku());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());

        // ÁNH XẠ ATTRIBUTE: Lặp qua bảng trung gian VariantProductAttribute
        if (entity.getAttributes() != null) {
            Set<AttributeResponseDTO> attributes = entity.getAttributes().stream()
                    .map(vpa -> attributeMapper.toDTO(vpa.getAttribute())) // Lấy Attribute Entity từ VPA
                    .collect(Collectors.toSet());

            dto.setAttributes(attributes);
        }
        if (entity.getImages() != null) {
            Set<VariantImageResponseDTO> images = entity.getImages().stream()
                    .map(variantImageMapper::toDTO)
                    .collect(Collectors.toSet());
            dto.setImages(images);
        }
        return dto;
    }
}