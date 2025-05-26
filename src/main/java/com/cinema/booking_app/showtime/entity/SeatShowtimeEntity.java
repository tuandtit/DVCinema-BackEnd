package com.cinema.booking_app.showtime.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.SeatStatus;
import com.cinema.booking_app.room.entity.SeatEntity;
import com.cinema.booking_app.user.entity.AccountEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tbl_seat_showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatShowtimeEntity extends AbstractAuditingEntity<Long> {
    @ManyToOne(optional = false)
    @JoinColumn(name = "seat_id")
    private SeatEntity seat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "showtime_id")
    private ShowtimeEntity showtime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountEntity user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE; // BOOKED, HOLD, AVAILABLE

    @Column(name = "canceled_time")
    private Instant canceledTime;

    private BigDecimal ticketPrice;
}
