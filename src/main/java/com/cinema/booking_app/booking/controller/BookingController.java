package com.cinema.booking_app.booking.controller;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.dto.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Response<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto request) {
        return Response.ok(bookingService.createBooking(request));
    }

    @GetMapping("/user/{userId}")
    public Response<List<BookingResponseDto>> getUserBookings(@PathVariable Long userId) {
        return Response.ok(bookingService.getUserBookings(userId));
    }
}
