package com.cinema.booking_app.booking.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.user.entity.AccountEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingEntity extends AbstractAuditingEntity<Long> {
    @Column(name = "booking_code", unique = true, nullable = false)
    String bookingCode;

    @Column(name = "booking_time")
    LocalDateTime bookingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    AccountEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id")
    ShowtimeEntity showtime;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketEntity> tickets = new HashSet<>();

    @Column(name = "total_price")
    BigDecimal totalPrice;

    @Column(name = "payment_status")
    String paymentStatus;
}