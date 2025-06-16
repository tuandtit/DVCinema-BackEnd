package com.cinema.booking_app.movie.dto.request.update;

import com.cinema.booking_app.common.enums.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieUpdateRequestDto {
    @NotNull(message = "Không bỏ trống id của movie cần cập nhật")
    Long movieId;

    @Size(min = 2, max = 100, message = "Tiêu đề phim phải từ 2 đến 100 ký tự")
    String title;

    @Size(min = 20, message = "Mô tả phim cần ít nhất 20 ký tự")
    String description;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0 phút")
    Integer duration;

    LocalDate releaseDate;

    Boolean isAvailableOnline;

    MultipartFile poster;

    String trailerUrl;

    String videoUrl;

    MovieStatus status;

    Long directorId;

    Set<Long> actorIds;

    Set<Long> genreIds;
}
