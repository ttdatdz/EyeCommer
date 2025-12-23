package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {



    boolean existsById(Long id);


    Optional<Supplier> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);


    boolean existsByPhone(String phone);

    Optional<Supplier> findByPhone(String phone);
}
