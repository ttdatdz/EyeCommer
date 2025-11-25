package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.mapper.ProductMapper;
import com.eyecommer.Backend.model.Category;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.model.ProductCategory;
import com.eyecommer.Backend.model.VariantProduct;
import com.eyecommer.Backend.repository.CategoryRepository;
import com.eyecommer.Backend.repository.ProductRepository;
import com.eyecommer.Backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl  implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        // 1. Ánh xạ từ DTO sang Entity (chưa có ID và các quan hệ phức tạp)
        Product product = productMapper.toEntity(request);

        // 2. Xử lý logic gán mối quan hệ ngược lại cho VariantProduct
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (VariantProduct variant : product.getVariants()) {
                variant.setProduct(product); // Gán mối quan hệ N-1 tới Product
            }
        } else {
            // Logic tạo 1 biến thể mặc định nếu Client không cung cấp
            VariantProduct defaultVariant = new VariantProduct();
            defaultVariant.setSku(product.getName().toUpperCase().replaceAll("\\s", "") + "-SKU");
            defaultVariant.setPrice(product.getPrice());
            defaultVariant.setStock(0);
            defaultVariant.setProduct(product); // Gán mối quan hệ
            product.getVariants().add(defaultVariant);
        }

        // 3. Xử lý ProductCategory (Danh mục) - Cần truy vấn CategoryRepository
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            // Tìm kiếm các Category Entity dựa trên List ID
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());

            Set<ProductCategory> productCategories = categories.stream()
                    .map(category -> {
                        ProductCategory pc = new ProductCategory();
                        pc.setProduct(product);    // Gán mối quan hệ N-1 tới Product
                        pc.setCategory(category);  // Gán mối quan hệ N-1 tới Category
                        pc.setIsDefault(false);
                        return pc;
                    })
                    .collect(Collectors.toSet());

            product.setProductCategories(productCategories);
        }

        // 4. Lưu Product. Nhờ CascadeType.ALL, tất cả các VariantProduct và ProductCategory
        // đã được gán (bước 2 & 3) sẽ được tự động lưu.
        return productMapper.toDTO(productRepository.save(product));
    }
}
