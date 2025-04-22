package com.cinema.booking_app.user.repository;

import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    List<RefreshTokenEntity> findByAccountAndRevoked(AccountEntity account, boolean revoked);
}