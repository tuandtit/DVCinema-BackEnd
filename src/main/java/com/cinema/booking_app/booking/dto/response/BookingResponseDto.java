package com.cinema.booking_app.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class BookingResponseDto {
    private Long id;
    private String bookingCode;
    private String bookingUrl;
    private String movieTitle;
    private String roomName;
    private LocalDate showDate;
    private LocalTime showTime;
    private Long userId;
    private List<TicketResponseDto> tickets;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private Instant createdDate;
}