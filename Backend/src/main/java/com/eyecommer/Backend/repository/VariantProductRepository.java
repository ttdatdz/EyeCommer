package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.VariantProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VariantProductRepository extends JpaRepository<VariantProduct, Long> {

    List<VariantProduct> findAllBySkuIn(Collection<String> skus);
}