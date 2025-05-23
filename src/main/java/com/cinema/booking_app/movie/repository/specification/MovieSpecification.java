package com.cinema.booking_app.movie.repository.specification;

import com.cinema.booking_app.common.enums.MovieStatus;
import com.cinema.booking_app.common.utils.TextUtils;
import com.cinema.booking_app.movie.entity.GenreEntity;
import com.cinema.booking_app.movie.entity.MovieEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MovieSpecification {
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_STATUS = "status";
    private final List<Specification<MovieEntity>> specifications = new ArrayList<>();

    public static MovieSpecification builder() {
        return new MovieSpecification();
    }

    public MovieSpecification withTitle(final String keyword) {
        if (!ObjectUtils.isEmpty(keyword)) {
            specifications.add(
                    (root, query, cb) ->
                            cb.like(cb.lower(cb.function("unaccent", String.class, root.get(FIELD_TITLE))),
                                    TextUtils.like(keyword))
            );
        }
        return this;
    }

    public MovieSpecification withStatuses(final List<MovieStatus> statuses) {
        if (!ObjectUtils.isEmpty(statuses)) {
            specifications.add(
                    (root, query, criteriaBuilder) ->
                            root.get(FIELD_STATUS).in(statuses)
            );
        }
        return this;
    }

    public MovieSpecification withGenreId(final Long genreId) {
        if (!ObjectUtils.isEmpty(genreId)) {
            specifications.add(
                    (root, query, criteriaBuilder) -> {
                        Join<MovieEntity, GenreEntity> genreJoin = root.join("genres", JoinType.INNER);

                        return criteriaBuilder.and(
                                criteriaBuilder.equal(genreJoin.get("id"), genreId),
                                criteriaBuilder.isTrue(genreJoin.get("isActive"))
                        );
                    }
            );
        }
        return this;
    }

    public Specification<MovieEntity> build() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(specifications.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toPredicate(root, query, criteriaBuilder)).toArray(Predicate[]::new));
    }
}
