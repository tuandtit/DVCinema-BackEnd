package com.cinema.booking_app.booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TicketResponseDto {
    private String seatName;
    private BigDecimal price;
}
