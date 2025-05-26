package com.cinema.booking_app.booking.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tbl_booking_seat_showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingSeatShowtimeEntity extends AbstractAuditingEntity<Long> {
    private Long bookingCode;
    private Long seatShowtimeId;
}