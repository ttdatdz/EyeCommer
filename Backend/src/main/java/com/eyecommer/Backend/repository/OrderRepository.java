package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Phương thức này kiểm tra OrderItem.variantProductId có trong list VariantProduct ID không,
    // và Order.status KHÔNG phải là COMPLETED/CANCELLED.
    @Query("SELECT COUNT(oi) FROM OrderItem oi JOIN oi.order o WHERE oi.variantProduct.id IN :variantIds AND o.status IN :pendingStatuses")
    long countPendingOrderItemsByVariantIds(
            @Param("variantIds") Set<Long> variantIds,
            @Param("pendingStatuses") List<String> pendingStatuses
    );
}