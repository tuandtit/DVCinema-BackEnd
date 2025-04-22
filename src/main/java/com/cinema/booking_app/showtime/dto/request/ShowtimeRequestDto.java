package com.cinema.booking_app.showtime.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeRequestDto {
    @NotNull
    Long movieId;
    @NotNull
    Long roomId;

    @NotNull
    @FutureOrPresent
    LocalDate showDate;

    @NotNull
    LocalTime showTime;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal ticketPrice;
}