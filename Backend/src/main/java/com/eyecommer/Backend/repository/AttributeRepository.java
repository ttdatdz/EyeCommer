package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    // Có thể thêm phương thức tìm kiếm theo tên nếu cần
    // Optional<Variant> findByName(String name);
    List<Attribute> findAllById(Iterable<Long> ids);
}