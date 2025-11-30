package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.VoucherUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherUserRepository extends JpaRepository<VoucherUser, Long> {

    boolean existsByVoucherIdAndUserId(Long voucherId, Long userId);

}
