package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.request.ProductUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.model.Product;

import java.util.List;

public interface ProductService {
    List<ProductResponseDTO> createProduct(List<ProductRequestDTO> productRequestDTO);
    // READ ALL (Sửa theo format phân trang/tìm kiếm)
    PageResponse<?> getAllProducts(int pageNo, int pageSize, String sortBy, String[] search);

    // READ DETAIL
    ProductResponseDTO getProductById(Long id);

    // UPDATE
    ProductResponseDTO updateProduct(Long id, ProductUpdateRequestDTO requestDTO);

    // DELETE
    void deleteProduct(Long id);
}
