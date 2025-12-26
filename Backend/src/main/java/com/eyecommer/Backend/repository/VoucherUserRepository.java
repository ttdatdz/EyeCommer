package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.VoucherUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherUserRepository extends JpaRepository<VoucherUser, Long> {

    boolean existsByVoucherIdAndUserId(Long voucherId, Long userId);

    Optional<VoucherUser> findByUser_IdAndVoucher_Id(Long userId, Long voucherId);
}
