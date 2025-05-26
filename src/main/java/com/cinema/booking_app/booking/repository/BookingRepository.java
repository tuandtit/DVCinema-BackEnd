package com.cinema.booking_app.booking.repository;

import com.cinema.booking_app.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    Optional<BookingEntity> findByBookingCode(Long bookingCode);

    void deleteByBookingCode(Long bookingCode);
}
