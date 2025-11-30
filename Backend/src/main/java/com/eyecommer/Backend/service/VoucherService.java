package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.VoucherRequestDTO;
import com.eyecommer.Backend.dto.request.VoucherUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.VoucherResponseDTO;
import com.eyecommer.Backend.model.VoucherUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoucherService {
    // CRUD Operations for Manager
    VoucherResponseDTO createVoucher(VoucherRequestDTO requestDTO);

    VoucherResponseDTO getVoucherById(Long id);

    VoucherResponseDTO updateVoucher(Long id, VoucherUpdateDTO updateDTO);

    void deleteVoucher(Long id);

    PageResponse<?> getAllVouchers(int pageNo, int pageSize, String sortBy, String[] search);

    VoucherResponseDTO claimVoucher (Long voucherId, Long userId);

    List<VoucherResponseDTO> getVouchersForCustomer(Long userId);
}