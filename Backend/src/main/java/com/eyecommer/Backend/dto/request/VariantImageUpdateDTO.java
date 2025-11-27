package com.eyecommer.Backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VariantImageUpdateDTO {
    // URL đã được upload lên Cloudinary/S3
    @NotBlank(message = "URL hình ảnh không được để trống.")
    private String imageUrl;

    // Đánh dấu ảnh chính (có thể là null)
    private Boolean isThumbnail;
}