package com.cinema.booking_app.room.repository;

import com.cinema.booking_app.room.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    boolean existsBySeatNumberAndRowId(String seatNumber, Long rowId);

    List<SeatEntity> findByRowId(Long rowId);
}
