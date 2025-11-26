package com.eyecommer.Backend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDTO {
    // 1. THÔNG TIN CƠ BẢN CỦA PRODUCT
    // Lưu ý: Đối với PUT, bạn nên gửi toàn bộ các trường này,
    // ngay cả khi không thay đổi. Nếu dùng PATCH, các trường này có thể là Optional.
    private String name;
    private String description;
    private Double price;
    private String status;
    private String thumbnailUrl;
    private String shortDescription;

    // 2. CẬP NHẬT DANH MỤC (N-M)
    // List ID mới sẽ GHI ĐÈ lên danh sách cũ
    private List<Long> categoryIds;

    // 3. CẬP NHẬT BIẾN THỂ (1-N)
    // List này phải bao gồm TẤT CẢ các biến thể mới và cũ.
    // Logic của Service sẽ dựa vào ID của từng VariantProductRequestDTO để biết nên UPDATE hay CREATE mới.
    private List<VariantProductUpdateDTO> variantProducts;
    // LƯU Ý: Phải sử dụng DTO riêng biệt cho Update Biến thể.
}