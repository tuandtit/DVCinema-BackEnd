package com.cinema.booking_app.room.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.SeatType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tbl_seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seatNumber", "row_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatEntity extends AbstractAuditingEntity<Long> {

    @Column(name = "seat_number")
    String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    SeatType seatType;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_id")
    RowEntity row;

    @Column(name = "is_booked")
    Boolean isBooked = false;

    Boolean isHeld = false;

    Boolean selected = false;

    @Column(name = "held_until")
    OffsetDateTime heldUntil;

    @Column(name = "selected_by_user_id")
    Long selectedByUserId;
}