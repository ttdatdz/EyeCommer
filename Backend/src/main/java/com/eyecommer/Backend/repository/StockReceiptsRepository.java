package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.StockReceipts;
import com.eyecommer.Backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReceiptsRepository extends JpaRepository<StockReceipts, Long> {
    boolean existsBySupplier(Supplier supplier);
}