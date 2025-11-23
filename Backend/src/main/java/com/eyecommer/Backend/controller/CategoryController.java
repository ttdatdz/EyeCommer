package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.CategoryRequestDTO;
import com.eyecommer.Backend.dto.response.CategoryResponseDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.model.Category;
import com.eyecommer.Backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    // Tiêm (Inject) Interface CategoryService. Spring sẽ tự động tìm CategoryServiceImpl.
    private final CategoryService categoryService;

    // ---CREATE (Tạo mới)---
    // POST /api/categories
    @PostMapping
    public ResponseData<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO request) {
        try {
            CategoryResponseDTO savedCategory = categoryService.save(request);
            return new ResponseData<>( HttpStatus.CREATED.value(),"Thêm danh mục thành công",savedCategory); // Mã 201
        } catch (Exception e) {
            // Xử lý lỗi nếu có (ví dụ: tên danh mục bị trùng unique constraint)
            return new ResponseData<>( HttpStatus.INTERNAL_SERVER_ERROR.value(),"Thêm danh mục thất bại vì: " +e.getMessage());
        }
    }

    // --- READ ALL (Lấy tất cả) ---
    // GET /api/categories
    @GetMapping
    public ResponseEntity<ResponseData<List<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.findAll();

        if (categories.isEmpty()) {
            ResponseData<List<CategoryResponseDTO>> response = new ResponseData<>(
                    HttpStatus.NO_CONTENT.value(),
                    "Không tìm thấy danh mục nào"
            );
            return new ResponseEntity<>(response, HttpStatus.OK); // Trả về 200 OK với data rỗng
        }

        ResponseData<List<CategoryResponseDTO>> response = new ResponseData<>(
                HttpStatus.OK.value(),
                "Lấy danh sách danh mục thành công",
                categories
        );
        return ResponseEntity.ok(response); // HTTP 200
    }

    // --- READ BY ID (Lấy theo ID)---
    // GET /api/categories/{id}
    @GetMapping("/{id}")
    public ResponseData<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        try {
            CategoryResponseDTO detailCategory = categoryService.findById(id);
            return new ResponseData<>( HttpStatus.CREATED.value(),"Lấy chi tiết danh mục thành công",detailCategory); // Mã 201
        } catch (Exception e) {
            // Xử lý lỗi nếu có (ví dụ: tên danh mục bị trùng unique constraint)
            return new ResponseData<>( HttpStatus.INTERNAL_SERVER_ERROR.value(),"Lấy chi tiết danh mục thất bại vì: " +e.getMessage());
        }
    }

    // --- UPDATE (Cập nhật)---
    // PUT /api/categories/{id}
    @PatchMapping("/{id}")
    public ResponseData<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDTO request)
    {
        try {
            CategoryResponseDTO updatedCategory = categoryService.update(id, request);
            return new ResponseData<>( HttpStatus.CREATED.value(),"Update danh mục thành công",updatedCategory); // Mã 201
        } catch (Exception e) {
            // Xử lý lỗi nếu có (ví dụ: tên danh mục bị trùng unique constraint)
            return new ResponseData<>( HttpStatus.INTERNAL_SERVER_ERROR.value(),"Update danh mục thất bại vì: " +e.getMessage());
        }
    }

    // --- DELETE (Xóa)---
    // DELETE /api/categories/{id}
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteById(id);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Xóa danh mục thành công");
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Xóa danh mục thất bại vì: "+e.getMessage()); // HTTP 404
        }
    }
}
