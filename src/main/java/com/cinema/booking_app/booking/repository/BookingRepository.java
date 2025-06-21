package com.cinema.booking_app.booking.repository;

import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.common.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    Optional<BookingEntity> findByBookingCode(Long bookingCode);

    void deleteByBookingCode(Long bookingCode);

    @Modifying
    @Query("UPDATE BookingEntity b SET b.bookingUrl = :url WHERE b.bookingCode = :code")
    void updateBookingUrl(@Param("code") Long bookingCode, @Param("url") String url);

    List<BookingEntity> findByAccountIdAndPaymentStatus(Long accountId, PaymentStatus paymentStatus);

    boolean existsByShowtimeId(Long showtimeId);
}
