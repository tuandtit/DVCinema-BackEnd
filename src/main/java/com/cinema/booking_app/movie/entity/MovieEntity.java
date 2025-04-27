package com.cinema.booking_app.movie.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.MovieStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_movies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "director_id"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieEntity extends AbstractAuditingEntity<Long> {
    @Column(name = "title")
    String title;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "duration")
    Integer duration;

    @Column(name = "release_date")
    LocalDate releaseDate;

    @Column(name = "is_available_online")
    Boolean isAvailableOnline;

    @Column(name = "poster_url")
    String posterUrl;

    @Column(name = "trailer_url")
    String trailerUrl;

    @Column(name = "video_url", columnDefinition = "TEXT")
    String videoUrl;

    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    MovieStatus status;

    // Nhiều diễn viên
    @ManyToMany
    @JoinTable(
            name = "tbl_movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<ContributorEntity> actors = new HashSet<>();

    // Một đạo diễn
    @ManyToOne
    @JoinColumn(name = "director_id")
    private ContributorEntity director;

    @ManyToMany
    @JoinTable(
            name = "tbl_movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<GenreEntity> genres = new HashSet<>();
}
