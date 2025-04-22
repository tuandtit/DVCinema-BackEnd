package com.cinema.booking_app.cinema.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "tbl_cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CityEntity extends AbstractAuditingEntity<Long> {
    @Column(unique = true)
    String name;

    @OneToMany(mappedBy = "city", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private List<CinemaEntity> cinemas;
}
