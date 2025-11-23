package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.CategoryRequestDTO;
import com.eyecommer.Backend.dto.response.CategoryResponseDTO;
import com.eyecommer.Backend.exception.ResourceNotFoundException;
import com.eyecommer.Backend.mapper.CategoryMapper;
import com.eyecommer.Backend.model.Category;
import com.eyecommer.Backend.repository.CategoryRepository;
import com.eyecommer.Backend.repository.ProductCategoryRepository;
import com.eyecommer.Backend.service.CategoryService; // Import Interface
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService { // Triển khai Interface

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final ProductCategoryRepository productCategoryRepository;

    // CREATE / UPDATE: Lưu (tạo mới hoặc cập nhật) danh mục
    @Override
    public CategoryResponseDTO save(CategoryRequestDTO request) {
        Category category = mapper.toEntity(request);
        categoryRepository.save(category);
        return mapper.toDTO(category);
    }

    // UPDATE
    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
        if (productCategoryRepository.existsByCategory_Id(id)) {
            throw new RuntimeException("Không thể sửa: Danh mục vẫn còn sản phẩm.");
        }
        // Cập nhật các trường từ DTO vào Entity
        if(request.getCategoryName()!=null){
            category.setName(request.getCategoryName());
        }
        if(request.getDescription()!=null){
            category.setDescription(request.getDescription());
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapper.toDTO(updatedCategory);
    }

    // READ All
    @Override
    public List<CategoryResponseDTO> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return mapper.toDTOList(categories);
    }

    // READ By ID
    @Override
    public CategoryResponseDTO findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Cannot find category with id "+id));
        CategoryResponseDTO response = mapper.toDTO(category);
        return response;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id " + id);
        }

        // Check nếu danh mục vẫn còn sản phẩm liên kết trong bảng ProductCategory
        if (productCategoryRepository.existsByCategory_Id(id)) {
            throw new RuntimeException("Không thể xóa: Danh mục vẫn còn sản phẩm.");
        }

        categoryRepository.deleteById(id);
    }

    // CHECK EXISTENCE
    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    // Phương thức nội bộ (nếu cần lấy Entity gốc)
    @Override
    public Optional<Category> getEntityById(Long id) {
        return categoryRepository.findById(id);
    }
}
