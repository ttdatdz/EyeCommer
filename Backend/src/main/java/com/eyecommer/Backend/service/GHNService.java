package com.eyecommer.Backend.service;

import com.eyecommer.Backend.dto.request.*;
import com.eyecommer.Backend.dto.response.*;
import com.eyecommer.Backend.model.Order;

import java.util.List;

public interface GHNService {

    void createShipment(Order order);

    void cancelShipment(Order order);

    List<ProvinceResponseDTO> getProvinces();

    List<DistrictResponseDTO> getDistricts(Integer provinceId);

    List<WardResponseDTO> getWards(Integer districtId);

    GHNLeadTimeResponse calculateLeadTime(GHNLeadTimeRequest request);

    List<GHNAvailableServiceResponse> getAvailableServices(
            GHNAvailableServiceRequest request
    );

    GHNFeeResponse calculateShippingFee(ShippingFeeEstimateRequest request);
}
