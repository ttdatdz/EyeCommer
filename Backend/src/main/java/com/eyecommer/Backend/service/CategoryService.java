package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.CategoryRequestDTO;
import com.eyecommer.Backend.dto.response.CategoryResponseDTO;
import com.eyecommer.Backend.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    // CREATE: Tạo mới danh mục
    CategoryResponseDTO save(CategoryRequestDTO request);

    // UPDATE: Cập nhật danh mục
    CategoryResponseDTO update(Long id, CategoryRequestDTO request);

    // READ All: Lấy tất cả danh mục (trả về DTO List)
    List<CategoryResponseDTO> findAll();

    // READ By ID: Lấy danh mục theo ID (trả về DTO Optional)
    CategoryResponseDTO findById(Long id);

    // DELETE: Xóa danh mục
    void deleteById(Long id);

    // Check Existence
    boolean existsById(Long id);

    // Phương thức nội bộ để lấy Entity gốc (dùng cho việc cập nhật)
    Optional<Category> getEntityById(Long id);
}