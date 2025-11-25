package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.VariantRequestDTO;
import com.eyecommer.Backend.dto.request.VariantUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.VariantResponseDTO;

import java.util.List;

public interface VariantService {
    // CREATE
    VariantResponseDTO createVariant(VariantRequestDTO requestDTO);

    // READ ALL

    PageResponse<?> getAllVariants(int pageNo, int pageSize, String sortBy, String[] search);
    // READ DETAIL
    VariantResponseDTO getVariantById(Long id);

    // UPDATE
    VariantResponseDTO updateVariant(Long id, VariantUpdateDTO requestDTO);

    // DELETE
    void deleteVariant(Long id);
}