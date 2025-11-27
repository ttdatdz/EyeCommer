package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.VariantImageRequestDTO;
import com.eyecommer.Backend.dto.request.VariantImageUpdateDTO;
import com.eyecommer.Backend.dto.response.VariantImageResponseDTO;
import com.eyecommer.Backend.model.VariantImage;
import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.stereotype.Component;

@Component
public class VariantImageMapper {
    public VariantImage toEntity(VariantImageRequestDTO dto, VariantProduct variantProduct) {
        if (dto == null) return null;

        VariantImage entity = new VariantImage();
        entity.setImageUrl(dto.getImageUrl());

        // Thiết lập giá trị mặc định cho isThumbnail
        entity.setIsThumbnail(dto.getIsThumbnail() != null ? dto.getIsThumbnail() : false);

        // Gán mối quan hệ N-1 ngược lại
        entity.setVariantProduct(variantProduct);

        return entity;
    }
    public VariantImage toEntity(VariantImageUpdateDTO dto, VariantProduct variantProduct) {
        if (dto == null) return null;

        VariantImage entity = new VariantImage();
        entity.setImageUrl(dto.getImageUrl());

        // Thiết lập giá trị mặc định cho isThumbnail
        entity.setIsThumbnail(dto.getIsThumbnail() != null ? dto.getIsThumbnail() : false);

        // Gán mối quan hệ N-1 ngược lại
        entity.setVariantProduct(variantProduct);

        return entity;
    }
    public VariantImageResponseDTO toDTO(VariantImage entity) {
        if (entity == null) return null;
        VariantImageResponseDTO dto = new VariantImageResponseDTO();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setIsThumbnail(entity.getIsThumbnail());
        return dto;
    }
}