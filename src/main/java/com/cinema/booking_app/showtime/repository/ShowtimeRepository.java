package com.cinema.booking_app.showtime.repository;

import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<ShowtimeEntity, Long> {
    @Query("""
    SELECT s FROM ShowtimeEntity s 
    WHERE s.movie.id = :movieId 
      AND s.room.cinema.id = :cinemaId 
      AND s.isActive = true 
      AND (
            s.showDate > :today OR 
            (s.showDate = :today AND s.startTime >= :now)
          )
""")
    List<ShowtimeEntity> findByMovieIdAndCinemaId(@Param("movieId") Long movieId,
                                               @Param("cinemaId") Long cinemaId,
                                               @Param("today") LocalDate today,
                                               @Param("now") LocalTime now);

    @Query("SELECT s.movie FROM ShowtimeEntity s WHERE s.showDate = :date AND s.room.cinema.id = :cinemaId AND s.isActive = true AND s.movie.status='NOW_SHOWING'")
    List<MovieEntity> findByShowDateAndCinemaId(LocalDate date, Long cinemaId);

    @Query("""
                SELECT s FROM ShowtimeEntity s
                WHERE (:cinemaId IS NULL OR s.room.cinema.id = :cinemaId)
                  AND (s.showDate = :showDate OR CAST(:showDate AS DATE) IS NULL )
            """)
    List<ShowtimeEntity> findByCinemaIdAndShowDate(
            @Param("cinemaId") Long cinemaId,
            @Param("showDate") LocalDate showDate
    );

    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM ShowtimeEntity s
    WHERE s.room.id = :roomId
      AND s.showDate = :showDate
      AND (
            (:newStartTime < s.endTime AND :newEndTime > s.startTime)
          )
""")
    boolean isOverlappingShowtime(
            @Param("roomId") Long roomId,
            @Param("showDate") LocalDate showDate,
            @Param("newStartTime") LocalTime newStartTime,
            @Param("newEndTime") LocalTime newEndTime
    );


}
