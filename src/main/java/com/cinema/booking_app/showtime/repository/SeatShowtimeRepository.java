package com.cinema.booking_app.showtime.repository;

import com.cinema.booking_app.showtime.entity.SeatShowtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SeatShowtimeRepository extends JpaRepository<SeatShowtimeEntity, Long> {


    List<SeatShowtimeEntity> findByShowtimeId(Long showtimeId);

    @Query("SELECT COUNT(s) " +
            "FROM SeatShowtimeEntity s " +
            "WHERE s.user.id = :userId " +
            "AND s.showtime.id = :showtimeId " +
            "AND s.status = 'HOLD'")
    int countBookedSeatsByUserAndShowtime(@Param("userId") Long userId,
                                          @Param("showtimeId") Long showtimeId);

    @Query("SELECT s " +
            "FROM SeatShowtimeEntity s " +
            "WHERE s.status = 'HOLD' AND s.canceledTime < :now")
    List<SeatShowtimeEntity> findExpiredSeats(@Param("now") Instant now);

}
