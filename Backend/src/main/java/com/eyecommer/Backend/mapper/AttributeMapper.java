package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.response.AttributeResponseDTO;
import com.eyecommer.Backend.model.Attribute;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttributeMapper {

    /**
     * Chuyển đổi VariantRequestDTO sang Variant Entity (toEntity).
     */
    public Attribute toEntity(VariantRequestDTO dto) {
        if (dto == null) return null;

        Attribute attribute = new Attribute();
        attribute.setName(dto.getName());
        attribute.setDescription(dto.getDescription());
        // Bỏ qua các mối quan hệ (variantProductAttributes)

        return attribute;
    }

    /**
     * Chuyển đổi Variant Entity sang VariantResponseDTO (toDTO).
     */
    public AttributeResponseDTO toDTO(Attribute entity) {
        if (entity == null) return null;

        AttributeResponseDTO dto = new AttributeResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    /**
     * Chuyển List<Variant Entity> sang List<VariantResponseDTO> (toDTOList).
     */
    public List<AttributeResponseDTO> toDTOList(List<Attribute> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}