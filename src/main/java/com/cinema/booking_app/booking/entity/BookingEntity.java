package com.cinema.booking_app.booking.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.PaymentStatus;
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
    Long bookingCode;
    BigDecimal totalPrice;
    LocalDateTime bookingTime;
    Long showtimeId;
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;
    String transactionId;
    String bookingUrl;
    boolean isUsed = false;
}