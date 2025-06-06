package com.cinema.booking_app.showtime.entity;

import com.cinema.booking_app.common.base.entity.AbstractAuditingEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_price_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPriceRuleEntity extends AbstractAuditingEntity<Long> {

    private Boolean isWeekend;

    private Boolean isEvening;

    private BigDecimal price;

    private Boolean isActive;
}
