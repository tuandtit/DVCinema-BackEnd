package com.cinema.booking_app.room.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "tbl_rows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"label", "room_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RowEntity extends AbstractAuditingEntity<Long> {

    @Column(nullable = false)
    String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    RoomEntity room;

    @OneToMany(mappedBy = "row", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatEntity> seats;
}