package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class VariantProductResponseDTO {
    private Long id;
    private String sku;
    private Double price;
    private Integer stock;
}
