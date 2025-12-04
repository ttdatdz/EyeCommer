package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.OrderSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface OrderSnapshotRepository extends JpaRepository<OrderSnapshot, Long> {

    Optional<OrderSnapshot> findByOrderCode(String orderCode);

    List<OrderSnapshot> findByUserId(Long userId);
}