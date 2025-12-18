package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Order;
import com.eyecommer.Backend.model.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipments, Long> {

    Optional<Shipments> findByShipmentCode(String shipmentCode);

    Optional<Shipments> findByOrder(Order order);

    Boolean existsByOrder(Order order);

//    Optional<Shipments> findByTrackingNumber(String trackingNumber);
}