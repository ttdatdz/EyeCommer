package com.eyecommer.Backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private String variantName;
    private Double price;
    private Integer quantity;
    private Double lineTotal;
}
