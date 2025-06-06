package com.cinema.booking_app.showtime.repository;

import com.cinema.booking_app.showtime.entity.TicketPriceRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface TicketPriceRuleRepository extends JpaRepository<TicketPriceRuleEntity, Long> {
    @Query("""
                SELECT r.price FROM TicketPriceRuleEntity r
                WHERE r.isWeekend = :isWeekend
                  AND r.isEvening = :isEvening
                  AND r.isActive = true
            """)
    Optional<BigDecimal> findTicketPriceByRule(
            @Param("isWeekend") boolean isWeekend,
            @Param("isEvening") boolean isEvening
    );
}