package com.cinema.booking_app.showtime.repository;

import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<ShowtimeEntity, Long> {
    @Query("SELECT s FROM ShowtimeEntity s WHERE s.movie.id = :movieId AND s.room.cinema.id = :cinemaId AND s.isActive = true")
    List<ShowtimeEntity> findByMovieIdAndCinemaId(@Param("movieId") Long movieId,
                                                  @Param("cinemaId") Long cinemaId);
    @Query("SELECT s.movie FROM ShowtimeEntity s WHERE s.showDate = :date AND s.room.cinema.id = :cinemaId AND s.isActive = true")
    List<MovieEntity> findByShowDateAndCinemaId(LocalDate date, Long cinemaId);
}
