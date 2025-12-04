package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class OrderItemSnapshotDTO {
    private Long productId;
    private String productName;
    private Long variantId;
    private String variantName;
    private String imageUrl;
    private Double priceAtPurchase;
    private Integer quantity;
    private Double lineTotal;
}
