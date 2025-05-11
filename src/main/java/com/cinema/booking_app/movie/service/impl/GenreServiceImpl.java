package com.cinema.booking_app.movie.service.impl;

import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.movie.dto.request.create.GenreRequestDto;
import com.cinema.booking_app.movie.dto.response.GenreResponseDto;
import com.cinema.booking_app.movie.entity.GenreEntity;
import com.cinema.booking_app.movie.repository.GenreRepository;
import com.cinema.booking_app.movie.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenreServiceImpl implements GenreService {
    GenreRepository genreRepository;

    @Override
    public GenreResponseDto create(GenreRequestDto dto) {
        GenreEntity entity = GenreEntity.builder()
                .name(dto.getName())
                .isActive(true)
                .build();
        return toDto(genreRepository.save(entity));
    }

    @Override
    public GenreResponseDto update(Long id, GenreRequestDto dto) {
        GenreEntity genre = genreRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Genre not found"));

        genre.setName(dto.getName());
        return toDto(genreRepository.save(genre));
    }

    @Override
    public void delete(Long id) {
        genreRepository.deleteById(id);
    }

    @Override
    public List<GenreResponseDto> getAll() {
        return genreRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private GenreResponseDto toDto(GenreEntity entity) {
        return GenreResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }


}
