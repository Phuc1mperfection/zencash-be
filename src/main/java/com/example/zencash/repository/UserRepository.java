package com.example.zencash.repository;

import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findById(UUID id);

    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :userId")
    void updateUserActiveStatus(@Param("userId") UUID userId, @Param("active") boolean active);
}
