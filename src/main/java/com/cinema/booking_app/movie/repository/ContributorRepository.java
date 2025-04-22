package com.cinema.booking_app.movie.repository;

import com.cinema.booking_app.common.enums.ContributorType;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContributorRepository extends JpaRepository<ContributorEntity, Long>, JpaSpecificationExecutor<ContributorEntity> {
    Optional<ContributorEntity> findByIdAndContributorType(Long id, ContributorType contributorType);

    boolean existsByStageName(String stageName);
}
