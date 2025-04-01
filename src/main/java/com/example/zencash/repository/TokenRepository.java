package com.example.zencash.repository;

import com.example.zencash.entity.Token;
import com.example.zencash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByRefreshToken(String refreshToken);

    void deleteByUser(User user);

    Optional<Token> findByTokenAndRevokedIsFalse(String token);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.token = :token")
    void revokeToken(String token);

}