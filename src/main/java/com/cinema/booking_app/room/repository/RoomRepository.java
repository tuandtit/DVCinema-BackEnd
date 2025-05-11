package com.cinema.booking_app.room.repository;

import com.cinema.booking_app.room.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    boolean existsByNameAndCinemaId(String name, Long cinemaId);

    @Query("SELECT r.id FROM RoomEntity r WHERE r.cinema.id = :cinemaId AND r.isActive = true")
    List<Long> findRoomIdsByCinemaId(@Param("cinemaId")Long cinemaId);
}
