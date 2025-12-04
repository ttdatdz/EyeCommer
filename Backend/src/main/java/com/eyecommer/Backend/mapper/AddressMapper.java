package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressMapper {

    // Tạo Address mới
    public Address toEntity(AddressRequestDTO req, User user) {
        Address a = new Address();
        a.setUser(user);
        a.setReceiverName(req.getReceiverName());
        a.setReceiverPhone(req.getReceiverPhone());
        a.setAddressDetail(req.getAddressDetail());
        a.setCity(req.getCity());
        a.setDistrict(req.getDistrict());
        a.setPostalCode(req.getPostalCode());
        a.setIsDefault(req.getIsDefault());
        return a;
    }

    // Update Address (KHÔNG được đổi user)
    public void updateFromDto(AddressRequestDTO req, Address a) {

        if (req.getReceiverName() != null) a.setReceiverName(req.getReceiverName());
        if (req.getReceiverPhone() != null) a.setReceiverPhone(req.getReceiverPhone());
        if (req.getAddressDetail() != null) a.setAddressDetail(req.getAddressDetail());
        if (req.getCity() != null) a.setCity(req.getCity());
        if (req.getDistrict() != null) a.setDistrict(req.getDistrict());
        if (req.getPostalCode() != null) a.setPostalCode(req.getPostalCode());
        if (req.getIsDefault() != null) a.setIsDefault(req.getIsDefault());
    }

    // Convert sang DTO
    public AddressResponseDTO toDTO(Address a) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(a.getId());
        dto.setUserId(a.getUser() != null ? a.getUser().getId() : null);
        dto.setReceiverName(a.getReceiverName());
        dto.setReceiverPhone(a.getReceiverPhone());
        dto.setAddressDetail(a.getAddressDetail());
        dto.setCity(a.getCity());
        dto.setDistrict(a.getDistrict());
        dto.setPostalCode(a.getPostalCode());
        dto.setIsDefault(a.getIsDefault());
        return dto;
    }

    public List<AddressResponseDTO> toDTOList(List<Address> list) {
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
