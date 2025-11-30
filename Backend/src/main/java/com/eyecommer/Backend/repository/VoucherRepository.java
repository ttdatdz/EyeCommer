package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.Voucher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // Phương thức custom để lấy các voucher đang có hiệu lực
    @Query("SELECT vu.voucher FROM VoucherUser vu WHERE vu.user.id = :userId")
    List<Voucher> findAllByUserId(Long userId);


//    @Lock(PESSIMISTIC_WRITE) = khóa bản ghi trong database, đảm bảo chỉ 1 request được đọc/sửa tại một thời điểm.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Voucher v WHERE v.id = :id")
    Voucher lockVoucherById( Long id);
}