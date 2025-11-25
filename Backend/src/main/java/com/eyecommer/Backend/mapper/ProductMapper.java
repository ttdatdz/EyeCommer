package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.response.CategoryResponseDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.dto.response.VariantProductResponseDTO;
import com.eyecommer.Backend.model.Category;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    // Tiêm VariantProductMapper để xử lý các biến thể
    @Autowired
    private VariantProductMapper variantProductMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * Chuyển đổi ProductCreateRequest sang Product Entity (dùng cho CREATE).
     */
    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;

        Product product = new Product();

        // --- Thông tin chung của Product (Ánh xạ trực tiếp) ---
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStatus(dto.getStatus());
        product.setThumbnailUrl(dto.getThumbnailUrl());
        product.setShortDescription(dto.getShortDescription());

        // --- Xử lý Biến thể (VariantProduct) ---
        // Ta ánh xạ các biến thể, nhưng CHƯA gán ngược lại 'product' cho chúng.
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            Set<VariantProduct> variants = dto.getVariants().stream()
                    .map(variantProductMapper::toEntity)
                    .collect(Collectors.toSet());

            // Gán Set biến thể vào Product
            product.setVariants(variants);
        } else {
            // Khởi tạo Set trống nếu không có biến thể nào được cung cấp.
            // Logic tạo biến thể mặc định sẽ nằm trong Service.
            product.setVariants(new HashSet<>());
        }

        // Bỏ qua các trường quan hệ ProductCategories vì logic này cần
        // truy vấn CategoryRepository, nên thuộc về Service Layer.

        return product;
    }

    public ProductResponseDTO toDTO(Product entity) {
        if (entity == null) return null;

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(entity.getId());

        // --- Ánh xạ các trường cơ bản ---
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        dto.setThumbnailUrl(entity.getThumbnailUrl());
        dto.setShortDescription(entity.getShortDescription());
        // Giả định AbstractEntity có các trường thời gian
        // dto.setCreatedAt(entity.getCreatedAt());
        // dto.setUpdatedAt(entity.getUpdatedAt());

        // --- Ánh xạ Biến thể (1-N) ---
        if (entity.getVariants() != null) {
            Set <VariantProduct> variantsProduct = entity.getVariants();
            Set<VariantProductResponseDTO> variants = variantsProduct.stream()
                    // 1. Sử dụng map để áp dụng hàm ánh xạ
                    .map(variantProductMapper::toDTO)
                    // 2. Thu thập kết quả thành Set mới
                    .collect(Collectors.toSet());
            dto.setVariants(variants);
        }

        // --- Ánh xạ Danh mục (N-M qua ProductCategory) ---
        if (entity.getProductCategories() != null) {

            // Loai bỏ dòng này: Set <Category> variantsProduct = entity.getProductCategories();

            Set<CategoryResponseDTO> categories = entity.getProductCategories().stream()
                    // SỬA: Gọi phương thức toDTO(ProductCategory pc) cục bộ
                    .map(pc -> categoryMapper.toDTO(pc.getCategory()))
                    .collect(Collectors.toSet());

            dto.setCategories(categories);
        }

        return dto;
    }

    /**
     * Chuyển đổi List<Product Entity> sang List<ProductResponse DTO>.
     * @param entities Danh sách Product Entity
     * @return Danh sách ProductResponse DTO
     */
    public List<ProductResponseDTO> toDTOList(List<Product> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}