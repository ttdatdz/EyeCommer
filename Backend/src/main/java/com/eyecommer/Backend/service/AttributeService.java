package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.request.VariantUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.AttributeResponseDTO;

public interface AttributeService {
    // CREATE
    AttributeResponseDTO createVariant(VariantRequestDTO requestDTO);

    // READ ALL

    PageResponse<?> getAllVariants(int pageNo, int pageSize, String sortBy, String[] search);
    // READ DETAIL
    AttributeResponseDTO getVariantById(Long id);

    // UPDATE
    AttributeResponseDTO updateVariant(Long id, VariantUpdateDTO requestDTO);

    // DELETE
    void deleteVariant(Long id);
}