package com.cinema.booking_app.booking.service;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request);

    BookingResponseDto getBookingByCode(String bookingCode);


}