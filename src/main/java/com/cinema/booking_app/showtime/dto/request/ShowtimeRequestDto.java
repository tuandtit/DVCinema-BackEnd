package com.cinema.booking_app.showtime.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    LocalTime startTime;
}