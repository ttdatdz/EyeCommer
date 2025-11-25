package com.eyecommer.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VariantRequestDTO {
    @NotBlank(message = "description must be not blank")
    private String name;
    @NotBlank(message = "description must be not blank")
    private String description;
}