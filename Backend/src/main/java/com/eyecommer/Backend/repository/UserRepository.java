package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query(value = "select r.name from Role r inner join UserHasRole ur on r.id = ur.role.id where ur.user.id=:userId")
    List<String> findAllRolesByUserId(Long userId);
}
