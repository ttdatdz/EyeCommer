package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantProductRepository extends JpaRepository<VariantProduct, Long> {
    // Không cần method tùy chỉnh cho thao tác CREATE
}