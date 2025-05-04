package com.cinema.booking_app.movie.service.impl;

import com.cinema.booking_app.common.base.service.CloudinaryService;
import com.cinema.booking_app.common.enums.ContributorType;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.movie.dto.request.create.MovieRequestDto;
import com.cinema.booking_app.movie.dto.request.search.MovieSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.MovieUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.MovieResponseDto;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import com.cinema.booking_app.movie.entity.GenreEntity;
import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.movie.mapper.MovieMapper;
import com.cinema.booking_app.movie.repository.ContributorRepository;
import com.cinema.booking_app.movie.repository.GenreRepository;
import com.cinema.booking_app.movie.repository.MovieRepository;
import com.cinema.booking_app.movie.service.MovieService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MovieServiceImpl implements MovieService {
    MovieRepository movieRepository;
    ContributorRepository contributorRepository;
    GenreRepository genreRepository;
    CloudinaryService cloudinaryService;

    MovieMapper movieMapper;

    @Override
    public MovieResponseDto create(MovieRequestDto dto) {
        ContributorEntity director = getDirectors(dto.getDirectorId());
        Set<ContributorEntity> actors = getActors(dto.getActorIds());
        Set<GenreEntity> genreEntities = getExistsGenres(dto.getGenreIds());

        MovieEntity movieEntity = movieMapper.toEntity(dto);

        validateDuplicateName(dto.getTitle(), dto.getDirectorId());
        movieEntity.setDirector(director);
        movieEntity.setActors(actors);
        movieEntity.setGenres(genreEntities);
        try {
            movieEntity.setPosterUrl(cloudinaryService.uploadImage(dto.getPoster()));
        } catch (IOException e) {
            throw new BusinessException("400", e.getMessage());
        }

        movieRepository.save(movieEntity);
        return movieMapper.toDto(movieEntity);
    }

    @Override
    public MovieResponseDto update(Long id, MovieUpdateRequestDto dto) {

        MovieEntity entity = getMovieById(id);
        movieMapper.update(dto, entity);

        Optional.ofNullable(dto.getDirectorId())
                .map(this::getDirectors)
                .ifPresent(entity::setDirector);

        Optional.ofNullable(dto.getActorIds())
                .map(this::getActors)
                .ifPresent(entity::setActors);

        Optional.ofNullable(dto.getGenreIds())
                .map(this::getExistsGenres)
                .ifPresent(entity::setGenres);

        return movieMapper.toDto(movieRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public MovieResponseDto getById(Long id) {
        return movieMapper.toDto(getMovieById(id));
    }

    @Override
    public Page<MovieResponseDto> getAll(MovieSearchRequest request) {
        return movieRepository.findAll(request.specification(), request.getPaging().pageable())
                .map(movieMapper::toDto);
    }

    private MovieEntity getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(
                        () -> new BusinessException(
                                String.valueOf(HttpStatus.NOT_FOUND.value()),
                                "Không tìm thấy phim với id: " + id
                        )
                );
    }

    private Set<ContributorEntity> getActors(Set<Long> actorIds) {
        return actorIds.stream()
                .map(id -> contributorRepository.findByIdAndContributorType(id, ContributorType.ACTOR)
                        .orElseThrow(() -> new BusinessException(String.valueOf(HttpStatus.NOT_FOUND.value()), "Không tìm thấy diễn viên với id: " + id)))
                .collect(Collectors.toSet());
    }

    private ContributorEntity getDirectors(Long id) {
        return contributorRepository.findByIdAndContributorType(id, ContributorType.DIRECTOR)
                .orElseThrow(() -> new BusinessException(String.valueOf(HttpStatus.NOT_FOUND.value()), "Không tìm thấy đạo diễn với id: " + id));

    }

    private Set<GenreEntity> getExistsGenres(Set<Long> genreIds) {
        return genreIds.stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(String.valueOf(HttpStatus.NOT_FOUND.value()), "Không tìm thấy thể loại phim với id: " + id)))
                .collect(Collectors.toSet());
    }

    private void validateDuplicateName(String name, Long directorId) {
        if (movieRepository.existsByTitleAndDirectorId(name, directorId)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Đã tồn tại phim với cùng tiêu đề của cùng đạo diễn: " + name);
        }
    }
}
