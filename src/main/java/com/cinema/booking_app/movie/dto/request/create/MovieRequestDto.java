package com.cinema.booking_app.movie.dto.request.create;

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
public class MovieRequestDto {

    @NotBlank(message = "Tiêu đề phim không được để trống")
    @Size(min = 2, max = 100, message = "Tiêu đề phim phải từ 2 đến 100 ký tự")
    String title;

    @NotBlank(message = "Mô tả phim không được để trống")
    @Size(min = 20, message = "Mô tả phim cần ít nhất 20 ký tự")
    String description;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0 phút")
    Integer duration;

    @NotNull(message = "Ngày phát hành không được để trống")
    LocalDate releaseDate;

    @NotNull(message = "Trạng thái xem online phải được xác định")
    Boolean isAvailableOnline;

    @NotNull(message = "Phim phải có poster")
    MultipartFile poster;

    String trailerUrl;

    String videoUrl;

    @NotNull(message = "Trạng thái phim không được để trống")
    MovieStatus status;

    @NotNull(message = "Phim phải có đạo diễn")
    Long directorId;

    @NotEmpty(message = "Phim phải có ít nhất 1 diễn viên")
    Set<Long> actorIds;

    @NotEmpty(message = "Phim phải có ít nhất 1 thể loại")
    Set<Long> genreIds;
}
