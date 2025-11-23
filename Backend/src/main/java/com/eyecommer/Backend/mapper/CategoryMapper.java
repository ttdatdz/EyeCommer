package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.request.CategoryRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.dto.response.CategoryResponseDTO;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) return null;

        Category category= new Category();
        category.setName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        return category;
    }

    public CategoryResponseDTO toDTO(Category entity) {
        if (entity == null) return null;

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setCategoryName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }
    // Chuyển List<Entity> sang List<DTO> (Sử dụng cho READ ALL)
    public List<CategoryResponseDTO> toDTOList(List<Category> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
