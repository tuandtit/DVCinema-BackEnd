package com.cinema.booking_app.movie.dto.request.search;

import com.cinema.booking_app.common.base.dto.request.FilterRequest;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import com.cinema.booking_app.movie.repository.specification.ContributorSpecification;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContributorSearchRequest extends FilterRequest<ContributorEntity> {

    String keyword;

    @Override
    public Specification<ContributorEntity> specification() {
        return ContributorSpecification.builder()
                .withNameOrStageName(keyword)
                .build();
    }
}
