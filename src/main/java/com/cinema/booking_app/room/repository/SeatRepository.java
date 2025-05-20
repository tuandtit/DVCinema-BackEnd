package com.cinema.booking_app.room.repository;

import com.cinema.booking_app.room.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    boolean existsBySeatNumberAndRowId(String seatNumber, Long rowId);


    @Query("""
                SELECT s FROM SeatEntity s
                WHERE s.isHeld = true
                  AND s.heldUntil IS NOT NULL
                  AND s.heldUntil < :now
            """)
    List<SeatEntity> findByIsHeldTrueAndHeldUntilBefore(@Param("now") OffsetDateTime now);

}
