package com.cinema.booking_app.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto {
    private Long id;
    private Long bookingCode;
    private String bookingUrl;
    private String cinemaName;
    private String roomName;
    private String seatName;
    private String movieTitle;
    private String showtime;
    private BigDecimal totalPrice;
    private String transactionId;
}