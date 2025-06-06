package com.cinema.booking_app.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingHistoryDto {
    private String bookingCode;
    private String movieName;
    private String cinemaName;
    private String showDateTime;
    private String seat;
    private BigDecimal totalAmount;
    private String packageDetails;
    private String bookingDate;
    private Integer pointsEarned;
    private String bookingUrl;
}