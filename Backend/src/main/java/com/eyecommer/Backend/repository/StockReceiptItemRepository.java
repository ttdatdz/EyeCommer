package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.StockReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReceiptItemRepository extends JpaRepository<StockReceiptItem, Long> {
}

