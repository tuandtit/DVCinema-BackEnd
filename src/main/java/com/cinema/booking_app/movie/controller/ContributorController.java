package com.cinema.booking_app.movie.controller;

import com.cinema.booking_app.common.base.dto.response.PagingResponse;
import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.movie.dto.request.create.ContributorCreateRequestDto;
import com.cinema.booking_app.movie.dto.request.search.ContributorSearchRequest;
import com.cinema.booking_app.movie.dto.request.update.ContributorUpdateRequestDto;
import com.cinema.booking_app.movie.dto.response.ContributorResponseDto;
import com.cinema.booking_app.movie.service.ContributorService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContributorController {
    ContributorService service;

    @PostMapping("/search")
    public Response<PagingResponse<ContributorResponseDto>> getAll(@RequestBody final ContributorSearchRequest request) {
        return Response.ok(PagingResponse.from(service.getAllContributors(request)));
    }

    @GetMapping("/{id}")
    public Response<ContributorResponseDto> getById(@PathVariable Long id) {
        return Response.ok(service.getContributorById(id));
    }

    @PostMapping
    public Response<ContributorResponseDto> create(@RequestBody @Valid ContributorCreateRequestDto dto) {
        return Response.created(service.createContributor(dto));
    }

    @PutMapping("/{id}")
    public Response<ContributorResponseDto> update(@PathVariable Long id, @RequestBody @Valid ContributorUpdateRequestDto dto) {
        return Response.ok(service.updateContributor(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        service.deleteContributor(id);
        return Response.noContent();
    }
}