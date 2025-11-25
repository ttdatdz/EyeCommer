package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.response.VariantResponseDTO;
import com.eyecommer.Backend.model.Variant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VariantMapper {

    /**
     * Chuyển đổi VariantRequestDTO sang Variant Entity (toEntity).
     */
    public Variant toEntity(VariantRequestDTO dto) {
        if (dto == null) return null;

        Variant variant = new Variant();
        variant.setName(dto.getName());
        variant.setDescription(dto.getDescription());
        // Bỏ qua các mối quan hệ (variantProductAttributes)

        return variant;
    }

    /**
     * Chuyển đổi Variant Entity sang VariantResponseDTO (toDTO).
     */
    public VariantResponseDTO toDTO(Variant entity) {
        if (entity == null) return null;

        VariantResponseDTO dto = new VariantResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    /**
     * Chuyển List<Variant Entity> sang List<VariantResponseDTO> (toDTOList).
     */
    public List<VariantResponseDTO> toDTOList(List<Variant> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}