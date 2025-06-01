package com.cinema.booking_app.room.entity;

import com.cinema.booking_app.cinema.entity.CinemaEntity;
import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

import java.util.List;

@Entity
@Table(name = "tbl_rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "cinema_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomEntity extends AbstractAuditingEntity<Long> {

    @Column(nullable = false)
    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    @JsonIgnore
    CinemaEntity cinema;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RowEntity> rows;

    @Column(name = "is_active")
    private Boolean isActive = true;
}