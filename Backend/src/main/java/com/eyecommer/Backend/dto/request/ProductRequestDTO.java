package com.eyecommer.Backend.dto.request;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {
    private String name;
    private String description;
    private Double price; // Giá cơ bản
    private String status;
    private String thumbnailUrl;
    private String shortDescription;

    // IDs của các danh mục liên quan
    private List<Long> categoryIds;

    // Danh sách các biến thể ban đầu
    private List<VariantProductRequestDTO> variantProducts;
}
