package com.eyecommer.Backend.controller;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.dto.response.ResponseData;
import com.eyecommer.Backend.model.Product;
import com.eyecommer.Backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * POST /api/products
     * Tạo mới một sản phẩm và các biến thể liên quan.
     * @param request Dữ liệu đầu vào để tạo sản phẩm
     * @return ResponseEntity<Product> chứa sản phẩm đã được tạo
     */
    @PostMapping
    public ResponseData<?> createProduct(@RequestBody ProductRequestDTO request) {
        try {

            ProductResponseDTO newProduct = productService.createProduct(request);

            return new ResponseData<>(HttpStatus.CREATED.value(), "Tạo Product thành công", newProduct);

        } catch (Exception e) {

            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Tạo Product thất bại vì: "+e.getMessage());
        }
    }

    // Các phương thức CRUD khác (GET, PUT, DELETE) sẽ được thêm vào đây sau
}