package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
    // 1. Tìm tất cả các liên kết vai trò của một User
    List<UserHasRole> findByUserId(Long userId); // Sửa từ String username thành Long userId

    // 2. Tìm kiếm liên kết cụ thể giữa User và Role (Nếu cần)
    Optional<UserHasRole> findByUserIdAndRoleName(Long userId, String roleName);
}
