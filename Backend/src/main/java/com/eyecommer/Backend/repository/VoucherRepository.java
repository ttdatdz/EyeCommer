package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    boolean existsByCode(String code);

    // Phương thức custom để lấy các voucher đang có hiệu lực
    List<Voucher> findAllByStartDateBeforeAndEndDateAfter(Date currentDate1, Date currentDate2);

    // Optional: Tìm voucher theo code để dùng trong luồng áp dụng
    // Optional<Voucher> findByCode(String code);
}