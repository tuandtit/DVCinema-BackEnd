package com.cinema.booking_app.movie.repository;

import com.cinema.booking_app.movie.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    Optional<GenreEntity> findByNameAndIsActive(String name, Boolean isActive);
}
