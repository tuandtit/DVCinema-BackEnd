package com.cinema.booking_app.showtime.repository;

import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowtimeRepository extends JpaRepository<ShowtimeEntity, Long> {
}
