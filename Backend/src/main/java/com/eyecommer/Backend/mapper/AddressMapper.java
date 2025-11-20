package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.AddressRequestDTO;
import com.eyecommer.Backend.dto.response.AddressResponseDTO;
import com.eyecommer.Backend.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(AddressRequestDTO dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setAddressDetail(dto.getAddressDetail());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setPostalCode(dto.getPostalCode());
        return address;
    }

    public AddressResponseDTO toDTO(Address entity) {
        if (entity == null) return null;

        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(entity.getId());
        dto.setAddressDetail(entity.getAddressDetail());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setPostalCode(entity.getPostalCode());
        return dto;
    }
}
