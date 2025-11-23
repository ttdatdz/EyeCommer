package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    boolean existsByCategory_Id(Long categoryId);
}
