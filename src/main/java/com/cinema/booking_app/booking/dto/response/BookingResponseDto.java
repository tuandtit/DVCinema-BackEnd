package com.cinema.booking_app.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponseDto {
    private Long id;
    private String bookingCode;
    private Long showtimeId;
    private String movieTitle;
    private String roomName;
    private LocalDateTime showDateTime;
    private Long userId;
    private List<String> seats;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private Instant createdDate;
}