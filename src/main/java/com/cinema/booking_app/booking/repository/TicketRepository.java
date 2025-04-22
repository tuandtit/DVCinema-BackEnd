package com.cinema.booking_app.booking.repository;

import com.cinema.booking_app.booking.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    @Query("SELECT t FROM TicketEntity t WHERE t.booking.showtime.id = :showtimeId")
    List<TicketEntity> findByShowtimeId(Long showtimeId);
}
