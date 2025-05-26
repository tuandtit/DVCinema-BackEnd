package com.cinema.booking_app.booking.repository;

import com.cinema.booking_app.booking.entity.BookingSeatShowtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingSeatShowtimeRepository extends JpaRepository<BookingSeatShowtimeEntity, Long> {
    @Query("SELECT b.seatShowtimeId FROM BookingSeatShowtimeEntity b WHERE b.bookingCode = :bookingCode")
    List<Long> findAllSeatShowtimeIdsByBookingCode(@Param("bookingCode")Long bookingCode);
}