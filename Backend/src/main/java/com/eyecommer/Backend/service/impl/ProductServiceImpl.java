package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.request.VariantProductRequestDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.mapper.ProductMapper;
import com.eyecommer.Backend.model.*;
import com.eyecommer.Backend.repository.CategoryRepository;
import com.eyecommer.Backend.repository.ProductRepository;
import com.eyecommer.Backend.repository.AttributeRepository;
import com.eyecommer.Backend.repository.VariantProductRepository;
import com.eyecommer.Backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final VariantProductRepository variantProductRepository;

    @Override
    @Transactional
    public List<ProductResponseDTO> createProduct(List<ProductRequestDTO> requests) {

        // 0. Validate SKU
        validateDuplicateSkus(requests);

        // 1. Load maps
        Map<Long, Category> categoryMap = loadCategoryMap(requests);
        Map<Long, Attribute> attributeMap = loadAttributeMap(requests);

        List<Product> savedProducts = new ArrayList<>();

        for (ProductRequestDTO req : requests) {
            Product product = productMapper.toEntity(req);

            processVariants(product, req, attributeMap);
            processCategories(product, req, categoryMap);

            savedProducts.add(productRepository.save(product));
        }

        return productMapper.toDTOList(savedProducts);
    }


    private void validateDuplicateSkus(List<ProductRequestDTO> requests) {
        Set<String> allSkus = new HashSet<>();

        for (ProductRequestDTO req : requests) {
            if (req.getVariantProducts() != null) {
                for (VariantProductRequestDTO v : req.getVariantProducts()) {
                    if (v.getSku() != null && !allSkus.add(v.getSku())) {
                        throw new RuntimeException("Lỗi: SKU trùng trong request: " + v.getSku());
                    }
                }
            }
        }

        List<VariantProduct> existing = variantProductRepository.findAllBySkuIn(allSkus);
        if (!existing.isEmpty()) {
            String existList = existing.stream()
                    .map(VariantProduct::getSku)
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Các SKU sau đã tồn tại: " + existList);
        }
    }


    private Map<Long, Category> loadCategoryMap(List<ProductRequestDTO> requests) {
        Set<Long> ids = new HashSet<>();

        for (ProductRequestDTO r : requests) {
            if (r.getCategoryIds() != null) {
                ids.addAll(r.getCategoryIds());
            }
        }

        return categoryRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Category::getId, c -> c));
    }


    private Map<Long, Attribute> loadAttributeMap(List<ProductRequestDTO> requests) {
        Set<Long> ids = new HashSet<>();

        for (ProductRequestDTO r : requests) {
            if (r.getVariantProducts() != null) {
                r.getVariantProducts().forEach(v ->
                        ids.addAll(v.getVariantAttributeIds())
                );
            }
        }

        return attributeRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Attribute::getId, a -> a));
    }


    private void processVariants(Product product, ProductRequestDTO req,
                                 Map<Long, Attribute> attributeMap) {

        if (req.getVariantProducts() == null || req.getVariantProducts().isEmpty()) {
            createDefaultVariant(product);
            return;
        }

        Set<VariantProduct> variants = new HashSet<>();

        for (VariantProduct variant : product.getVariants()) {
            variant.setProduct(product);

            VariantProductRequestDTO original = req.getVariantProducts().stream()
                    .filter(v -> v.getSku().equals(variant.getSku()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy DTO variant của SKU: " + variant.getSku())
                    );

            Set<VariantProductAttribute> attrs = new HashSet<>();

            for (Long attrId : original.getVariantAttributeIds()) {
                Attribute attr = attributeMap.get(attrId);
                if (attr == null)
                    throw new RuntimeException("Attribute ID không hợp lệ: " + attrId);

                VariantProductAttribute vpa = new VariantProductAttribute();
                vpa.setVariantProduct(variant);
                vpa.setAttribute(attr);
                attrs.add(vpa);
            }

            variant.setAttributes(attrs);
            variants.add(variant);
        }

        product.setVariants(variants);
    }


    private void createDefaultVariant(Product product) {
        VariantProduct defaultVariant = new VariantProduct();

        String safeName = (product.getName() == null || product.getName().isEmpty())
                ? "UNKNOWN_PRODUCT"
                : product.getName();

        defaultVariant.setSku(safeName.toUpperCase().replaceAll("\\s", "") + "-SKU");
        defaultVariant.setPrice(product.getPrice());
        defaultVariant.setStock(0);
        defaultVariant.setProduct(product);

        product.getVariants().add(defaultVariant);
    }


    private void processCategories(Product product, ProductRequestDTO req,
                                   Map<Long, Category> categoryMap) {

        if (req.getCategoryIds() == null || req.getCategoryIds().isEmpty()) return;

        Set<ProductCategory> pcs = new HashSet<>();

        for (Long id : req.getCategoryIds()) {
            Category category = categoryMap.get(id);
            if (category == null)
                throw new RuntimeException("Category ID không hợp lệ: " + id);

            ProductCategory pc = new ProductCategory();
            pc.setProduct(product);
            pc.setCategory(category);
            pc.setIsDefault(false);
            pcs.add(pc);
        }

        product.setProductCategories(pcs);
    }
}
