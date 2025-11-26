package com.eyecommer.Backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class VariantProductUpdateDTO {

    // ID của biến thể (Bắt buộc cho thao tác UPDATE/DELETE SKU cũ)
    // Trường này là tùy chọn (Optional) nếu người dùng đang TẠO MỚI một SKU trong danh sách Update
    private Long id;

    @NotBlank(message = "Mã SKU không được để trống.")
    private String sku;

    @NotNull(message = "Giá bán của biến thể không được để trống.")
    private Double price;

    @NotNull(message = "Tồn kho của biến thể không được để trống.")
    private Integer stock;

    // IDs của các thuộc tính (Attribute) tạo nên SKU này.
    @NotNull(message = "Danh sách thuộc tính (Variant Attributes) không được thiếu.")
    private List<Long> variantAttributeIds;

    // Danh sách hình ảnh. Cần @NotNull nếu bạn coi việc có hình ảnh là bắt buộc.
    @NotNull(message = "Danh sách hình ảnh biến thể không được thiếu.")
    private List<VariantImageRequestDTO> images;
}