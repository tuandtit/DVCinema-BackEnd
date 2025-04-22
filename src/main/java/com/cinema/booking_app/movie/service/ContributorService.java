package com.cinema.booking_app.movie.service;


import com.cinema.booking_app.movie.dto.request.create.ContributorCreateRequestDto;
import com.cinema.booking_app.movie.dto.request.search.ContributorSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.ContributorUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.ContributorResponseDto;
import org.springframework.data.domain.Page;

public interface ContributorService {
    ContributorResponseDto createContributor(final ContributorCreateRequestDto requestDto);

    ContributorResponseDto getContributorById(final Long id);

    Page<ContributorResponseDto> getAllContributors(final ContributorSearchRequest request);

    ContributorResponseDto updateContributor(Long id, final ContributorUpdateRequestDto requestDto);

    void deleteContributor(Long id);
}
