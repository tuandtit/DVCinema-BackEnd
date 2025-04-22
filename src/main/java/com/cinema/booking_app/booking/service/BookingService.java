package com.cinema.booking_app.booking.service;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request);

    List<BookingResponseDto> getUserBookings(Long userId);
}