package com.cinema.booking_app.booking.service;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import jakarta.transaction.Transactional;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request);

    @Transactional
    BookingResponseDto confirmPayment(Long id);

    void deleteBooking(Long id);

    BookingResponseDto getBookingByCode(Long bookingCode);
}