package com.eyecommer.Backend.dto.request;

import lombok.Data;

@Data
public class VariantImageRequestDTO {
    // URL đã được upload lên Cloudinary/S3
    private String imageUrl;

    // Đánh dấu ảnh chính (có thể là null)
    private Boolean isThumbnail;
}