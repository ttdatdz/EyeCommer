package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.ProductRequestDTO;
import com.eyecommer.Backend.dto.response.ProductResponseDTO;
import com.eyecommer.Backend.model.Product;

import java.util.List;

public interface ProductService {
    List<ProductResponseDTO> createProduct(List<ProductRequestDTO> productRequestDTO);
}
