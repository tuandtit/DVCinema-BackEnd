package com.cinema.booking_app.movie.dto.response;

import com.cinema.booking_app.common.enums.ContributorType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContributorResponseDto {
    Long id;
    String name;
    String photoUrl;
    String stageName;
    String gender;
    LocalDate birthDate;
    String introduction;
    ContributorType contributorType;
}