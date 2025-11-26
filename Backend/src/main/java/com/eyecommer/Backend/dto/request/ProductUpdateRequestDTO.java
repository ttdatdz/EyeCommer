package com.eyecommer.Backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDTO {

    // 1. THÔNG TIN CƠ BẢN CỦA PRODUCT

    @NotBlank(message = "Tên sản phẩm không được để trống.")
    private String name;

    @NotBlank(message = "Mô tả chi tiết không được để trống.")
    private String description;

    @NotNull(message = "Giá cơ bản không được để trống.")
    private Double price;

    @NotBlank(message = "Trạng thái sản phẩm không được để trống.")
    private String status;

    @NotBlank(message = "URL hình ảnh thumbnail không được để trống.")
    private String thumbnailUrl;

    @NotBlank(message = "Mô tả ngắn không được để trống.")
    private String shortDescription;

    // 2. CẬP NHẬT DANH MỤC (N-M)

    @NotNull(message = "Danh sách Category IDs không được thiếu.")
    private List<Long> categoryIds;

    // 3. CẬP NHẬT BIẾN THỂ (1-N)

    // Đảm bảo list biến thể không bị thiếu và validate từng phần tử bên trong
    @NotNull(message = "Danh sách biến thể không được để trống.")
    @Valid // Annotation này cần thiết để kích hoạt validation trên các phần tử List bên trong (VariantProductUpdateDTO)
    private List<VariantProductUpdateDTO> variantProducts;
}