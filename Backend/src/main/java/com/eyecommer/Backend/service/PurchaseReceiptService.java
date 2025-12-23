package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.PurchaseReceiptCreateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.PurchaseReceiptResponseDTO;

public interface PurchaseReceiptService {

    void create(PurchaseReceiptCreateRequestDTO request);

//    void updateStatus(PurchaseReceiptUpdateStatusRequestDTO request);

    PageResponse<?> getAll(int pageNo, int pageSize, String sortBy, String[] search);

    PurchaseReceiptResponseDTO getDetail(Long id);
}
