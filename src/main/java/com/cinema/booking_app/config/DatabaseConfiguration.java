package com.cinema.booking_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({
        "com.cinema.booking_app.user.repository",
        "com.cinema.booking_app.booking.repository",
        "com.cinema.booking_app.movie.repository",
        "com.cinema.booking_app.room.repository",
        "com.cinema.booking_app.showtime.repository",
        "com.cinema.booking_app.cinema.repository"
})
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {
}
