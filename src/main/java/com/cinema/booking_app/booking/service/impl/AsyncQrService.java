package com.cinema.booking_app.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncQrService {

    private final BookingQrService bookingQrService;

    @Async
    public void generateAndUploadQRCodeAsync(Long bookingCode) {
        bookingQrService.generateAndUploadQRCodeTransactional(bookingCode);
    }
}