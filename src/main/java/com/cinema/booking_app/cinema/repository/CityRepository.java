package com.cinema.booking_app.cinema.repository;

import com.cinema.booking_app.cinema.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    boolean existsByName(String name);
}
