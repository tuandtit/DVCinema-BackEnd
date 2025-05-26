package com.cinema.booking_app.booking.controller;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.dto.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public Response<BookingResponseDto> getBookingByCode(@RequestParam Long bookingCode) {
        return Response.ok(bookingService.getBookingByCode(bookingCode));
    }
}
