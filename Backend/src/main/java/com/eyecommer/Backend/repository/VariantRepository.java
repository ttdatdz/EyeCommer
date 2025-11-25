package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
    // Có thể thêm phương thức tìm kiếm theo tên nếu cần
    // Optional<Variant> findByName(String name);
}