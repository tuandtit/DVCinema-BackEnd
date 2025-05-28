package com.cinema.booking_app.booking.controller;

import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.dto.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public Response<BookingResponseDto> confirmAndGetBookingByCode(@RequestParam Long bookingCode) {
        return Response.ok(bookingService.confirmAndGetBookingByCode(bookingCode));
    }

    @DeleteMapping
    public Response<Void> delete(@RequestParam Long bookingCode) {
        bookingService.deleteBooking(bookingCode);
        return Response.noContent();
    }
}
