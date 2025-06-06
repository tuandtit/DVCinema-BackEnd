package com.cinema.booking_app.booking.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
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
    Long accountId;
    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;
    String bookingUrl;
    boolean isUsed = false;
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> seatShowtimeEntities = new ArrayList<>();
}