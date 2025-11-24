package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Long> {
}
