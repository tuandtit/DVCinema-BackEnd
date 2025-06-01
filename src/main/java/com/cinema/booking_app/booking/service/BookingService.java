package com.cinema.booking_app.booking.service;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.dto.response.TicketDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request);

    @Transactional
    void confirmPayment(Long id);

    @Transactional
    void deleteBooking(Long bookingCode);

    @Transactional
    BookingResponseDto confirmAndGetBookingByCode(Long bookingCode);

    List<TicketDto> checkin(Long bookingCode);
}