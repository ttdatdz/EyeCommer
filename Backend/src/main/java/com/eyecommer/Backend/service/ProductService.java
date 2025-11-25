package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.model.Product;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
}
