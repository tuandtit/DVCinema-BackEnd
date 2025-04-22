package com.cinema.booking_app.showtime.controller;

import com.cinema.booking_app.showtime.dto.request.ShowtimeRequestDto;
import com.cinema.booking_app.showtime.dto.response.ShowtimeResponseDto;
import com.cinema.booking_app.showtime.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ShowtimeResponseDto> create(@RequestBody @Valid ShowtimeRequestDto dto) {
        return ResponseEntity.ok(showtimeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ShowtimeResponseDto>> getAll() {
        return ResponseEntity.ok(showtimeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        showtimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}