package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;

import java.util.List;

public interface AddressService {

    AddressResponseDTO createAddress(Long userId, AddressRequestDTO dto);

    AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO dto);

    void deleteAddress(Long addressId);

    AddressResponseDTO getAddressById(Long addressId);

    List<AddressResponseDTO> getAddressesByUser(Long userId);
}
