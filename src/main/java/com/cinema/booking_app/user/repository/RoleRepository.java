package com.cinema.booking_app.user.repository;

import com.cinema.booking_app.common.enums.ERole;
import com.cinema.booking_app.user.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole eRole);
}