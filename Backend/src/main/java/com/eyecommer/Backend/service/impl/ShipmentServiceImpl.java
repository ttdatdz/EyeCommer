package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.GHNWebhookRequest;
import com.eyecommer.Backend.mapper.ShipmentMapper;
import com.eyecommer.Backend.model.Order;
import com.eyecommer.Backend.model.Shipments;
import com.eyecommer.Backend.repository.ShipmentRepository;
import com.eyecommer.Backend.service.ShipmentService;
import com.eyecommer.Backend.utils.OrderStatus;
import com.eyecommer.Backend.utils.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;

    @Override
    @Transactional
    public void handleGHNWebhook(GHNWebhookRequest request) {
        Shipments shipment = shipmentRepository.findByShipmentCode(request.getOrder_code())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus(shipmentMapper.mapGHNStatus(request.getStatus()));
        shipmentRepository.save(shipment);
    }
}
