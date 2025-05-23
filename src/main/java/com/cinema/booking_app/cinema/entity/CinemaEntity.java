package com.cinema.booking_app.cinema.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.cinema.booking_app.room.entity.RoomEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "tbl_cinemas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "city_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaEntity extends AbstractAuditingEntity<Long> {

    @Column(nullable = false)
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    @JsonIgnore
    CityEntity city;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomEntity> rooms;
}