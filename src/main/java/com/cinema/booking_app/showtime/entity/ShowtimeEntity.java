package com.cinema.booking_app.showtime.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.room.entity.RoomEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_show_times")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeEntity extends AbstractAuditingEntity<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Column(name = "show_date")
    private LocalDate showDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private BigDecimal ticketPrice;

    @Column(nullable = false)
    private boolean isActive = true;

}
