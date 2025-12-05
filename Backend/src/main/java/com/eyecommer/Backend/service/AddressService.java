package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.dto.response.PageResponse;

import java.util.List;

public interface AddressService {


    AddressResponseDTO createAddress(Long userId, AddressRequestDTO request);

    AddressResponseDTO updateAddress(Long addressId, Long userId, AddressRequestDTO request);

    void deleteAddress(Long addressId, Long userId);


    AddressResponseDTO getById(Long id, Long userId);

    PageResponse<?> getAllByUser(Long userId, int pageNo, int pageSize, String sortBy, String[] search);
}
