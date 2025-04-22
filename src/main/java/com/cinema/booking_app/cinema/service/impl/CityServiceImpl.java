package com.cinema.booking_app.cinema.service.impl;

import com.cinema.booking_app.cinema.dto.request.CityRequestDto;
import com.cinema.booking_app.cinema.dto.response.CityResponseDto;
import com.cinema.booking_app.cinema.entity.CityEntity;
import com.cinema.booking_app.cinema.mapper.CityMapper;
import com.cinema.booking_app.cinema.repository.CityRepository;
import com.cinema.booking_app.cinema.service.CityService;
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
public class CityServiceImpl implements CityService {
    CityRepository cityRepository;
    CityMapper cityMapper;

    @Override
    public CityResponseDto create(CityRequestDto dto) {
        validateDuplicateName(dto.getName());

        return saveAndReturnDto(cityMapper.toEntity(dto));
    }

    @Override
    public CityResponseDto update(Long id, CityRequestDto dto) {
        validateDuplicateName(dto.getName());
        CityEntity city = existsCity(id);
        cityMapper.update(dto, city);

        return saveAndReturnDto(city);
    }

    @Override
    public void delete(Long id) {
        cityRepository.deleteById(id);
    }

    @Override
    public CityResponseDto getById(Long id) {
        return cityMapper.toDto(existsCity(id));
    }

    @Override
    public List<CityResponseDto> getAll() {
        return cityMapper.toDto(cityRepository.findAll());
    }

    private CityEntity existsCity(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy thành phố có id: " + id));
    }

    private void validateDuplicateName(String name) {
        if (cityRepository.existsByName(name)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Đã tồn tại thành phố có tên: " + name);
        }
    }

    private CityResponseDto saveAndReturnDto(CityEntity city) {
        return cityMapper.toDto(cityRepository.save(city));
    }
}
