package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.VariantProduct;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VariantProductRepository extends JpaRepository<VariantProduct, Long> {

    List<VariantProduct> findAllBySkuIn(Collection<String> skus);
    List<VariantProduct> findAllByIdIn(Collection<String> skus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VariantProduct v where v.id = :id")
    Optional<VariantProduct> findByIdForUpdate(Long id);




}