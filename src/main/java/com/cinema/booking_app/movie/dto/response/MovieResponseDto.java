package com.cinema.booking_app.movie.dto.response;

import com.cinema.booking_app.common.enums.MovieStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponseDto {
    Long id;

    String title;

    String description;

    Integer duration; // phút

    LocalDate releaseDate;

    Boolean isAvailableOnline;

    String posterUrl;

    String trailerUrl;

    String videoUrl;

    MovieStatus status; // COMING_SOON, NOW_SHOWING, ENDED

    ContributorResponseDto director;

    Set<ContributorResponseDto> actors;

    Set<GenreResponseDto> genres;
}
