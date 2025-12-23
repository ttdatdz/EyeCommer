package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.SupplierCreateRequestDTO;
import com.eyecommer.Backend.dto.request.SupplierUpdateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.SupplierResponseDTO;

public interface SupplierService {
    SupplierResponseDTO create(SupplierCreateRequestDTO request);

    SupplierResponseDTO update(Long id, SupplierUpdateRequestDTO request);

    void delete(Long id);

    PageResponse<?> getAllSuppliers(int pageNo, int pageSize, String sortBy, String[] search);

    SupplierResponseDTO getDetail(Long id);
}
