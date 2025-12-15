package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.ConfirmOrderRequestDTO;
import com.eyecommer.Backend.dto.request.GHNCreateOrderRequest;
import com.eyecommer.Backend.dto.response.DistrictResponseDTO;
import com.eyecommer.Backend.dto.response.GHNCreateOrderResponse;
import com.eyecommer.Backend.dto.response.ProvinceResponseDTO;
import com.eyecommer.Backend.dto.response.WardResponseDTO;
import com.eyecommer.Backend.mapper.GHNMapper;
import com.eyecommer.Backend.model.Order;
import com.eyecommer.Backend.model.Shipments;
import com.eyecommer.Backend.repository.ShipmentRepository;
import com.eyecommer.Backend.service.GHNService;
import com.eyecommer.Backend.utils.GHNClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GHNServiceImpl implements GHNService {

    private final GHNClient ghnClient;
    private final ShipmentRepository shipmentRepository;
    private final GHNMapper ghnMapper;

    // ===== LOCATION APIs =====

    @Override
    public List<ProvinceResponseDTO> getProvinces() {
        return ghnClient.getProvinces();
    }

    @Override
    public List<DistrictResponseDTO> getDistricts(Integer provinceId) {
        return ghnClient.getDistricts(provinceId);
    }

    @Override
    public List<WardResponseDTO> getWards(Integer districtId) {
        return ghnClient.getWards(districtId);
    }

    // ===== SHIPPING =====
    @Override
    public void createShipment(Order order, ConfirmOrderRequestDTO request) {

        GHNCreateOrderRequest ghnReq =
                ghnMapper.toCreateOrderRequest(order, request);

        GHNCreateOrderResponse response =
                ghnClient.createOrder(ghnReq);

        Shipments shipment = new Shipments();
        shipment.setOrder(order);
        shipment.setCarrier("GHN");
        shipment.setShipmentCode(response.getData().getOrder_code());
        shipment.setShippingFee(response.getData().getTotal_fee());
        shipment.setStatus("CREATED");

        shipmentRepository.save(shipment);
    }

    @Override
    public void cancelShipment(Order order) {

        Shipments shipment =
                shipmentRepository.findByOrder(order)
                        .orElseThrow(() -> new RuntimeException("Shipment not found"));

        ghnClient.cancelOrder(shipment.getShipmentCode());
        shipment.setStatus("CANCELLED");
    }
}



