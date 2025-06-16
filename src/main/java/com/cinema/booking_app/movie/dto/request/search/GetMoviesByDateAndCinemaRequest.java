package com.cinema.booking_app.movie.dto.request.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetMoviesByDateAndCinemaRequest {
    private LocalDate date;
    private Long cinemaId;
}
