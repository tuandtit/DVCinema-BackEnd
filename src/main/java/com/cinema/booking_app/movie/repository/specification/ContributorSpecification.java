package com.cinema.booking_app.movie.repository.specification;

import com.cinema.booking_app.common.enums.ContributorType;
import com.cinema.booking_app.common.utils.TextUtils;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContributorSpecification {

    private static final String FIELD_STAGE_NAME = "stageName";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CONTRIBUTOR_TYPE = "contributorType";

    private final List<Specification<ContributorEntity>> specifications = new ArrayList<>();

    public static ContributorSpecification builder() {
        return new ContributorSpecification();
    }

    public ContributorSpecification withNameOrStageName(final String keyword) {
        if (!ObjectUtils.isEmpty(keyword)) {
            specifications.add(
                    (root, query, cb) ->
                            cb.or(
                                    cb.like(
                                            cb.lower(cb.function("unaccent", String.class, root.get(FIELD_STAGE_NAME))),
                                            TextUtils.like(keyword)
                                    ),
                                    cb.like(
                                            cb.lower(cb.function("unaccent", String.class, root.get(FIELD_NAME))),
                                            TextUtils.like(keyword)
                                    )
                            )
            );
        }
        return this;
    }

    public ContributorSpecification withContributorType(final ContributorType type) {
        if (!ObjectUtils.isEmpty(type)) {
            specifications.add(
                    (root, query, cb) ->
                            cb.equal(root.get(FIELD_CONTRIBUTOR_TYPE),type)
            );
        }
        return this;
    }

    public Specification<ContributorEntity> build() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(specifications.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toPredicate(root, query, criteriaBuilder)).toArray(Predicate[]::new));
    }
}
