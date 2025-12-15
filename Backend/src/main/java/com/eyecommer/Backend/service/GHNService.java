package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.response.DistrictResponseDTO;
import com.eyecommer.Backend.dto.response.ProvinceResponseDTO;
import com.eyecommer.Backend.dto.response.WardResponseDTO;
import com.eyecommer.Backend.model.Order;

import java.util.List;

public interface GHNService {

    void createShipment(Order order, ConfirmOrderRequestDTO request);

    void cancelShipment(Order order);

    List<ProvinceResponseDTO> getProvinces();

    List<DistrictResponseDTO> getDistricts(Integer provinceId);
    List<WardResponseDTO> getWards(Integer districtId);
}
