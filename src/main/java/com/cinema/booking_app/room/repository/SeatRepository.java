package com.cinema.booking_app.room.repository;

import com.cinema.booking_app.room.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    boolean existsBySeatNumberAndRowId(String seatNumber, Long rowId);
}
