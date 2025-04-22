package com.cinema.booking_app.movie.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.common.enums.ContributorType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_contributors")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContributorEntity extends AbstractAuditingEntity<Long> {
    @Column(name = "name")
    String name;

    @Column(name = "photo_url")
    String photoUrl;

    @Column(name = "stage_name", unique = true)
    String stageName;
    String gender;

    @Column(name = "birth_date")
    LocalDate birthDate;
    @Column(columnDefinition = "TEXT")
    String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "contributor_type")
    private ContributorType contributorType; // ACTOR or DIRECTOR
}