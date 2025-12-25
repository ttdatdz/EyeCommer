package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndVariantProductId(Long cartId, Long variantProductId);

    List<CartItem> findByIdInAndCart_User_Id(
            Collection<Long> ids,
            Long userId
    );
    Optional<CartItem> findByCart_User_IdAndVariantProduct_Id(
            Long userId,
            Long variantProductId
    );
}
