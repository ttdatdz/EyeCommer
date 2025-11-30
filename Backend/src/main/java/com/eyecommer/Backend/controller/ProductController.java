package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.request.ProductUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseData<?> createProduct(@RequestBody List<ProductRequestDTO>  request) {
        try {

            List<ProductResponseDTO> listNewProduct = productService.createProduct(request);

            return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo Product thành công", listNewProduct);

        } catch (Exception e) {

            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Tạo Product thất bại vì: "+e.getMessage());
        }
    }

    // 2. READ ALL (GET ALL) - Dùng format phân trang/tìm kiếm
    @GetMapping
    public ResponseData<?> getAllProducts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "") String[] search) {
        try {
            PageResponse<?> pageResponse = productService.getAllProducts(pageNo, pageSize, sortBy, search);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách Product thành công", pageResponse);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lấy danh sách Product thất bại vì: " + e.getMessage());
        }
    }

    // 3. READ DETAIL (GET by ID)
    @GetMapping("/{id}")
    public ResponseData<?> getProductDetail(@PathVariable Long id) {
        try {
            ProductResponseDTO product = productService.getProductById(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Lấy chi tiết Product thành công", product);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.NOT_FOUND.value(), "Lấy chi tiết Product thất bại vì: " + e.getMessage());
        }
    }

    // 4. UPDATE (PUT)
    @PutMapping("/{id}")
    public ResponseData<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequestDTO requestDTO) {
        try {
            ProductResponseDTO updatedProduct = productService.updateProduct(id, requestDTO);
            return new ResponseData<>(HttpStatus.OK.value(), "Cập nhật Product thành công", updatedProduct);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Cập nhật Product thất bại vì: " + e.getMessage());
        }
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Xóa Product thành công");
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Xóa Product thất bại vì: " + e.getMessage());
        }
    }
}