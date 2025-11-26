package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.response.CategoryResponseDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.dto.response.VariantProductResponseDTO;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.eyecommer.Backend.model.ProductCategory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Autowired
    private VariantProductMapper variantProductMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * Chuyển đổi ProductCreateRequest sang Product Entity (toEntity).
     */
    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStatus(dto.getStatus());
        product.setThumbnailUrl(dto.getThumbnailUrl());
        product.setShortDescription(dto.getShortDescription());

        // --- XỬ LÝ BIẾN THỂ (Chỉ một lần duy nhất) ---
        Set<VariantProduct> variants = new HashSet<>();
        if (dto.getVariantProducts() != null && !dto.getVariantProducts().isEmpty()) {
            variants = dto.getVariantProducts().stream()
                    .map(variantProductMapper::toEntity)
                    .collect(Collectors.toSet());
        }
        product.setVariants(variants);

        // Khởi tạo Set Category an toàn (Service sẽ ghi đè sau)
        product.setProductCategories(new HashSet<>());

        return product;
    }

    // ************ PHƯƠNG THỨC ÁNH XẠ MỐI QUAN HỆ ************

    // ... (Giữ nguyên các hàm toDTO khác) ...
    // Note: Cần đảm bảo CategoryResponseDTO có setIsDefault()

    public CategoryResponseDTO toDTO(ProductCategory pc) {
        if (pc == null || pc.getCategory() == null) return null;

        CategoryResponseDTO dto = categoryMapper.toDTO(pc.getCategory());

        if (dto != null) {
            // Giả định: Bạn đã sửa CategoryResponseDTO để có setIsDefault
            // Nếu không, dòng này sẽ gây lỗi biên dịch.
            // dto.setIsDefault(pc.getIsDefault());
        }
        return dto;
    }

    public ProductResponseDTO toDTO(Product entity) {
        if (entity == null) return null;

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        dto.setThumbnailUrl(entity.getThumbnailUrl());
        dto.setShortDescription(entity.getShortDescription());

        // --- Ánh xạ Biến thể (1-N) ---
        if (entity.getVariants() != null) {
            Set <VariantProduct> variantsProduct = entity.getVariants();
            Set<VariantProductResponseDTO> variants = variantsProduct.stream()
                    .map(variantProductMapper::toDTO)
                    .collect(Collectors.toSet());
            dto.setVariantProducts(variants);
        }

        // --- Ánh xạ Danh mục (N-M qua ProductCategory) ---
        if (entity.getProductCategories() != null) {
            Set<CategoryResponseDTO> categories = entity.getProductCategories().stream()
                    // SỬ DỤNG LAMBDA ĐỂ TRÁNH LỖI PHÂN GIẢI
                    .map(pc -> this.toDTO(pc))
                    .collect(Collectors.toSet());
            dto.setCategories(categories);
        }

        return dto;
    }

    public List<ProductResponseDTO> toDTOList(List<Product> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}