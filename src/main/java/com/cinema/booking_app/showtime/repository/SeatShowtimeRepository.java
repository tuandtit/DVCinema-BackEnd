package com.cinema.booking_app.showtime.repository;

import com.cinema.booking_app.showtime.entity.SeatShowtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatShowtimeRepository extends JpaRepository<SeatShowtimeEntity, Long> {


    List<SeatShowtimeEntity> findByShowtimeId(Long showtimeId);

    @Modifying
    @Query("DELETE FROM SeatShowtimeEntity sse WHERE sse.showtime.id = :showtimeId AND sse.seat.id IN :seatIds")
    void deleteByShowtimeIdAndSeatIds(@Param("showtimeId") Long showtimeId, @Param("seatIds") List<Long> seatIds);
}
