package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.PurchaseReceiptCreateRequestDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.PurchaseReceiptResponseDTO;
import com.eyecommer.Backend.utils.PurchaseReceiptStatus;

public interface PurchaseReceiptService {

    PurchaseReceiptResponseDTO create(PurchaseReceiptCreateRequestDTO request);

    PurchaseReceiptResponseDTO updateStatus(Long receiptId, PurchaseReceiptStatus newStatus);

    PageResponse<?> getAll(int pageNo, int pageSize, String sortBy, String[] search);

    PurchaseReceiptResponseDTO getDetail(Long id);
}
