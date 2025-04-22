package com.cinema.booking_app.movie.service.impl;

import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.movie.dto.request.create.ContributorCreateRequestDto;
import com.cinema.booking_app.movie.dto.request.search.ContributorSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.ContributorUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.ContributorResponseDto;
import com.cinema.booking_app.movie.entity.ContributorEntity;
import com.cinema.booking_app.movie.mapper.ContributorMapper;
import com.cinema.booking_app.movie.repository.ContributorRepository;
import com.cinema.booking_app.movie.service.ContributorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContributorServiceImpl implements ContributorService {
    ContributorRepository contributorRepository;
    ContributorMapper contributorMapper;

    @Override
    public ContributorResponseDto createContributor(ContributorCreateRequestDto requestDto) {
        validateDuplicateName(requestDto.getStageName());
        ContributorEntity entity = contributorMapper.toEntity(requestDto);
        return contributorMapper.toDto(contributorRepository.save(entity));
    }

    @Override
    public ContributorResponseDto getContributorById(Long id) {
        ContributorEntity entity = existsContributor(id);
        return contributorMapper.toDto(entity);
    }

    @Override
    public Page<ContributorResponseDto> getAllContributors(ContributorSearchRequest request) {
        return contributorRepository.findAll(request.specification(), request.getPaging().pageable())
                .map(contributorMapper::toDto);
    }

    @Override
    public ContributorResponseDto updateContributor(Long id, ContributorUpdateRequestDto requestDto) {
        ContributorEntity contributorEntity = existsContributor(id);
        validateDuplicateName(requestDto.getStageName());
        contributorMapper.update(requestDto, contributorEntity);

        return contributorMapper.toDto(contributorRepository.save(contributorEntity));
    }

    @Override
    public void deleteContributor(Long id) {
        contributorRepository.deleteById(id);
    }

    private ContributorEntity existsContributor(Long id) {
        return contributorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(String.valueOf(HttpStatus.NOT_FOUND.value()), "Không tìm thấy contributor với id: " + id));
    }

    private void validateDuplicateName(String stageName) {
        if (contributorRepository.existsByStageName(stageName)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Nghệ danh contributor đã tồn tại: " + stageName);
        }
    }
}
