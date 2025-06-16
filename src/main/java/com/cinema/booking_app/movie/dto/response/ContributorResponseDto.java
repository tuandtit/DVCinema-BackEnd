package com.cinema.booking_app.movie.dto.response;

import com.cinema.booking_app.common.enums.ContributorType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
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