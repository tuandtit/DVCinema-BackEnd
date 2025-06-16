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

    String posterUrl;

    String trailerUrl;

    String videoUrl;

    Set<ContributorResponseDto> actors;

    ContributorResponseDto director;

    Integer duration; // phút

    LocalDate releaseDate;

    Boolean isAvailableOnline;

    MovieStatus status; // COMING_SOON, NOW_SHOWING, ENDED

    Set<GenreResponseDto> genres;
}
