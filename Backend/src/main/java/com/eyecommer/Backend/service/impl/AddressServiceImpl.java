package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.mapper.AddressMapper;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.AddressRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponseDTO createAddress(Long userId, AddressRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressMapper.toEntity(dto);
        address.setUser(user);

        Address saved = addressRepository.save(address);
        return addressMapper.toDTO(saved);
    }

    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO dto) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setAddressDetail(dto.getAddressDetail());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setPostalCode(dto.getPostalCode());

        Address saved = addressRepository.save(address);
        return addressMapper.toDTO(saved);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.delete(address);
    }

    @Override
    public AddressResponseDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        return addressMapper.toDTO(address);
    }

    @Override
    public List<AddressResponseDTO> getAddressesByUser(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toDTO)
                .toList();
    }
}
