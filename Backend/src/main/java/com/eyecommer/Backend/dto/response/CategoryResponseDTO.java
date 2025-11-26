package com.eyecommer.Backend.dto.response;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDTO {
    private Long id;
    private String categoryName;
    private String description;
    private Boolean isDefault;
}
