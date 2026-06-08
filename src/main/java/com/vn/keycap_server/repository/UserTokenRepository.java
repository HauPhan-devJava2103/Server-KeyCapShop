package com.vn.keycap_server.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vn.keycap_server.modal.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    boolean existsByRefreshTokenAndIsRevokedFalse(String refreshToken);

    Optional<UserToken> findByRefreshToken(String refreshToken);

    @Modifying
    @Query("DELETE FROM UserToken u WHERE u.expiresAt < :now")
    int deleteAllExpiredTokens(@Param("now") Date now);
}
