package com.cinema.booking_app.booking.dto.request;

import com.cinema.booking_app.common.enums.PaymentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequestDto {
    private Long id;
    PaymentStatus paymentStatus;
    String transactionId;
    Long showtimeId;
    Long accountId;

    @NotEmpty(message = "seatShowtimeIds cannot be empty")
    private Set<Long> seatShowtimeIds;

    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;
}