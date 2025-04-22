package com.cinema.booking_app.movie.dto.request.update;

import com.cinema.booking_app.common.enums.MovieStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieUpdateRequestDto {

    @Size(min = 2, max = 100, message = "Tiêu đề phim phải từ 2 đến 100 ký tự")
    String title;

    @Size(min = 20, message = "Mô tả phim cần ít nhất 20 ký tự")
    String description;

    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0 phút")
    Integer duration;

    LocalDate releaseDate;

    Boolean isAvailableOnline;

    String posterUrl;

    String trailerUrl;

    String videoUrl;

    MovieStatus status;

    Set<Long> actorIds;

    Long directorId;

    Set<Long> genreIds;
}
