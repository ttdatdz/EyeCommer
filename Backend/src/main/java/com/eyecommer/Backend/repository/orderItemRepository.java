package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface orderItemRepository extends JpaRepository<OrderItem, Long> {
}
