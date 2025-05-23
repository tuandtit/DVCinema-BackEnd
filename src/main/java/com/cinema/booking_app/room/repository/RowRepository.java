package com.cinema.booking_app.room.repository;

import com.cinema.booking_app.room.entity.RowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RowRepository extends JpaRepository<RowEntity, Long> {
    boolean existsByLabelAndRoomId(String label, Long roomId);
    List<RowEntity> findByRoomId(Long roomId);
}
