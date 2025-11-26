package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.request.VariantUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.dto.response.AttributeResponseDTO;
import com.eyecommer.Backend.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/variants")
public class VariantController {

    @Autowired
    private AttributeService attributeService;

    // 1. CREATE (POST)
    @PostMapping
    public ResponseData<?> createVariant(@RequestBody VariantRequestDTO requestDTO) {
        try {
            AttributeResponseDTO response = attributeService.createVariant(requestDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo Thuộc tính (Variant) thành công", response);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Tạo Thuộc tính (Variant) thất bại vì: " + e.getMessage());
        }
    }

    // 2. READ ALL (GET)
    @GetMapping
    public ResponseData<?> getAllVariants(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {
        try {
            // Gọi Service với các tham số phân trang, sắp xếp, tìm kiếm
            PageResponse<?> pageResponse = attributeService.getAllVariants(pageNo, pageSize, sortBy, search);

            // Mã 200 OK vì đây là thao tác lấy dữ liệu thành công
            return new ResponseData<>( HttpStatus.OK.value(),"Lấy danh sách Thuộc tính thành công", pageResponse);

        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return new ResponseData<>( HttpStatus.INTERNAL_SERVER_ERROR.value(),"Lấy danh sách Thuộc tính thất bại vì: " + e.getMessage());
        }
    }

    // 3. READ DETAIL (GET by ID)
    @GetMapping("/{id}")
    public ResponseData<?> getVariantById(@PathVariable Long id) {
        try {
            AttributeResponseDTO response = attributeService.getVariantById(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy chi tiết Thuộc tính thành công", response);
        } catch (Exception e) {
            // Thường là ResourceNotFoundException
            return new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Lấy chi tiết Thuộc tính thất bại vì: " + e.getMessage());
        }
    }

    // 4. UPDATE (PUT)
    @PatchMapping("/{id}")
    public ResponseData<?> updateVariant(
            @PathVariable Long id,
            @RequestBody VariantUpdateDTO requestDTO) {
        try {
            AttributeResponseDTO response = attributeService.updateVariant(id, requestDTO);
            return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật Thuộc tính thành công", response);
        } catch (Exception e) {
            // Thường là ResourceNotFoundException hoặc lỗi Validation
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Cập nhật Thuộc tính thất bại vì: " + e.getMessage());
        }
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteVariant(@PathVariable Long id) {
        try {
            attributeService.deleteVariant(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Xóa Thuộc tính thành công");
        } catch (Exception e) {
            // Thường là ResourceNotFoundException hoặc lỗi Khóa ngoại (Constraint Violation)
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Xóa Thuộc tính thất bại vì: " + e.getMessage());
        }
    }
}