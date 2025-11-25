package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Không cần method tùy chỉnh cho thao tác CREATE
}