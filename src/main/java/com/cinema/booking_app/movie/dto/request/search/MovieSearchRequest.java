package com.cinema.booking_app.movie.dto.request.search;

import com.cinema.booking_app.common.base.dto.request.FilterRequest;
import com.cinema.booking_app.common.enums.MovieStatus;
import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.movie.repository.specification.MovieSpecification;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieSearchRequest extends FilterRequest<MovieEntity> {
    String query;
    List<MovieStatus> status = new ArrayList<>();

    @Override
    public Specification<MovieEntity> specification() {
        return MovieSpecification.builder()
                .withTitle(query)
                .withStatuses(status)
                .build();
    }
}
