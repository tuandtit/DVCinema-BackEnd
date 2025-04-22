package com.cinema.booking_app.booking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BookingRequestDto {
    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Seats cannot be empty")
    private List<Long> seatIds; // Danh sách ID của ghế

    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;
}