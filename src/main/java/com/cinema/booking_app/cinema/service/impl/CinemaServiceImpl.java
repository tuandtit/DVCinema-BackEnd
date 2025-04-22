package com.cinema.booking_app.cinema.service.impl;

import com.cinema.booking_app.cinema.dto.request.CinemaRequestDto;
import com.cinema.booking_app.cinema.dto.request.CinemaUpdateDto;
import com.cinema.booking_app.cinema.dto.response.CinemaResponseDto;
import com.cinema.booking_app.cinema.entity.CinemaEntity;
import com.cinema.booking_app.cinema.entity.CityEntity;
import com.cinema.booking_app.cinema.mapper.CinemaMapper;
import com.cinema.booking_app.cinema.repository.CinemaRepository;
import com.cinema.booking_app.cinema.repository.CityRepository;
import com.cinema.booking_app.cinema.service.CinemaService;
import com.cinema.booking_app.common.error.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CinemaServiceImpl implements CinemaService {
    CinemaRepository cinemaRepository;
    CityRepository cityRepository;

    CinemaMapper cinemaMapper;

    @Override
    public CinemaResponseDto create(CinemaRequestDto dto) {
        CityEntity city = existsCity(dto.getCityId());
        validateDuplicateName(dto.getName(), city.getId());

        CinemaEntity cinema = cinemaMapper.toEntity(dto);
        cinema.setCity(city);
        return saveAndReturnDto(cinema);
    }

    @Override
    public CinemaResponseDto update(Long id, CinemaUpdateDto dto) {
        CinemaEntity cinema = existsCinema(id);

        String updatedName = dto.getName() != null ? dto.getName() : cinema.getName();
        Long updatedCityId = dto.getCityId() != null ? dto.getCityId() : cinema.getCity().getId();

        validateDuplicateName(updatedName, updatedCityId);

        cinema.setName(updatedName);
        cinema.setCity(existsCity(updatedCityId));

        return saveAndReturnDto(cinema);
    }

    @Override
    public void delete(Long id) {
        cinemaRepository.deleteById(id);
    }

    @Override
    public CinemaResponseDto getById(Long id) {
        return cinemaMapper.toDto(existsCinema(id));
    }

    @Override
    public List<CinemaResponseDto> getAll() {
        return cinemaMapper.toDto(cinemaRepository.findAll());
    }

    private CinemaEntity existsCinema(Long id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy rạp chiếu phim với id: " + id));
    }

    private CityEntity existsCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy thành phố với id: " + id));
    }

    private void validateDuplicateName(String name, Long cityId) {
        if (cinemaRepository.existsByNameAndCityId(name, cityId)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Trong thành phố đã tồn tại rạp chiếu phim có tên: " + name);
        }
    }

    private CinemaResponseDto saveAndReturnDto(CinemaEntity cinema) {
        return cinemaMapper.toDto(cinemaRepository.save(cinema));
    }
}
