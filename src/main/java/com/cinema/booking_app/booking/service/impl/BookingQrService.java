package com.cinema.booking_app.booking.service.impl;

import com.cinema.booking_app.booking.repository.BookingRepository;
import com.cinema.booking_app.common.base.service.impl.QRCodeService;
import com.cinema.booking_app.common.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BookingQrService {

    private final BookingRepository bookingRepository;

    private final QRCodeService qrCodeService;

    @Transactional
    public void generateAndUploadQRCodeTransactional(Long bookingCode) {
        try {
            String qrUrl = qrCodeService.generateAndUploadQRCode(bookingCode);
            bookingRepository.updateBookingUrl(bookingCode, qrUrl);
        } catch (IOException e) {
            throw new BusinessException("400", "Có lỗi khi tạo mã qr và upload");

        }
    }
}
