package com.cinema.booking_app.cinema.repository;

import com.cinema.booking_app.cinema.entity.CinemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaRepository extends JpaRepository<CinemaEntity, Long> {
    boolean existsByNameAndCityId(String name, Long cityId);
}
