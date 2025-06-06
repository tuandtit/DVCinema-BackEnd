package com.cinema.booking_app.room.controller;

import com.cinema.booking_app.common.base.dto.response.Response;
import com.cinema.booking_app.room.dto.request.create.RoomRequestDto;
import com.cinema.booking_app.room.dto.request.update.RoomUpdateDto;
import com.cinema.booking_app.room.dto.response.RoomResponseDto;
import com.cinema.booking_app.booking.dto.response.SeatsDto;
import com.cinema.booking_app.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService service;

    @PostMapping
    public Response<RoomResponseDto> create(@Valid @RequestBody RoomRequestDto dto) {
        return Response.created(service.create(dto));
    }

    @PutMapping("/{id}")
    public Response<RoomResponseDto> update(@PathVariable Long id,
                                            @Valid @RequestBody RoomUpdateDto dto) {
        return Response.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Response.noContent();
    }

    @GetMapping("/seats-of-room")
    public Response<List<SeatsDto>> getSeatsById(@RequestParam Long roomId) {
        return Response.ok(service.getSeatsByRoomId(roomId));
    }

    @GetMapping("")
    public Response<List<RoomResponseDto>> getAll() {
        return Response.ok(service.getAll());
    }
}
