package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class VariantImageResponseDTO {
    private Long id;
    private String imageUrl;
    private Boolean isThumbnail; // Đánh dấu ảnh chính (ví dụ: true nếu là ảnh đại diện SKU)
}