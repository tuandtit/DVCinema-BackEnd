package com.cinema.booking_app.movie.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_genres")
@SQLDelete(sql = "UPDATE tbl_genres SET is_active = false WHERE id = ?")
public class GenreEntity extends AbstractAuditingEntity<Long> {
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_active")
    Boolean isActive = true;
}
