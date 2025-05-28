package com.cinema.booking_app.booking.service.impl;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.booking.mapper.BookingMapper;
import com.cinema.booking_app.booking.repository.BookingRepository;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.service.impl.MailService;
import com.cinema.booking_app.common.base.service.impl.QRCodeService;
import com.cinema.booking_app.common.enums.PaymentStatus;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.showtime.entity.SeatShowtimeEntity;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.SeatShowtimeRepository;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    SeatShowtimeRepository seatShowtimeRepository;
    BookingMapper bookingMapper;
    QRCodeService qrCodeService;
    MailService mailService;
    ShowtimeRepository showtimeRepository;
    AccountRepository accountRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) {
        // Tạo booking
        Long bookingCode = generateBookingCode();
        BookingEntity booking;
        try {
            booking = bookingRepository.save(BookingEntity.builder()
                    .bookingUrl(qrCodeService.generateAndUploadQRCode(bookingCode))
                    .bookingCode(bookingCode)
                    .showtimeId(getShowtime(request.getShowtimeId()).getId())
                    .accountId(getAccount(request.getAccountId()).getId())
                    .totalPrice(request.getTotalPrice())
                    .paymentStatus(PaymentStatus.PENDING)
                    .isUsed(false)
                    .build());
        } catch (IOException e) {
            throw new BusinessException("400", "Có lỗi khi tạo mã qr và upload");
        }

        seatShowtimeRepository.setBookingCode(bookingCode, request.getSeatShowtimeIds());

        // Ánh xạ sang DTO
        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public void confirmPayment(Long bookingCode) {
        BookingEntity booking = getBooking(bookingCode);
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setBookingTime(LocalDateTime.now());
        mailService.sendTicketEmail("duongtuan10122003@gmail.com", booking.getBookingUrl());
        seatShowtimeRepository.confirmBook(bookingCode);
        bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long bookingCode) {
        bookingRepository.deleteByBookingCode(bookingCode);
    }

    @Transactional
    @Override
    public BookingResponseDto confirmAndGetBookingByCode(Long bookingCode) {
        BookingEntity booking = getBooking(bookingCode);
        if (booking.getPaymentStatus() != PaymentStatus.SUCCESS) {
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
            booking.setBookingTime(LocalDateTime.now());
            mailService.sendTicketEmail("duongtuan10122003@gmail.com", booking.getBookingUrl());
            seatShowtimeRepository.confirmBook(bookingCode);
            bookingRepository.save(booking);
        }

        BookingResponseDto dto = bookingMapper.toDto(booking);
        ShowtimeEntity showtime = showtimeRepository.findById(booking.getShowtimeId())
                .orElseThrow(() -> new BusinessException("404", "Showtime not found with id: " + booking.getShowtimeId()));
        List<SeatShowtimeEntity> seatShowtimeEntities = seatShowtimeRepository.findByBooking_BookingCode(bookingCode);
        String seatName = String.join(",",
                seatShowtimeEntities.stream()
                        .map(entity -> entity.getSeat().getRow().getLabel() + entity.getSeat().getSeatNumber())
                        .toList());
        dto.setCinemaName(showtime.getRoom().getCinema().getName());
        dto.setMovieTitle(showtime.getMovie().getTitle());
        dto.setSeatName(seatName);
        dto.setRoomName(showtime.getRoom().getName());
        dto.setShowtime(getShowDateTimeFormatted(showtime.getShowDate(), showtime.getStartTime()));

        return dto;
    }

    private String getShowDateTimeFormatted(LocalDate showDate, LocalTime startTime) {
        if (showDate == null || startTime == null) {
            return null; // hoặc throw exception nếu cần
        }
        LocalDateTime dateTime = LocalDateTime.of(showDate, startTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm");
        return dateTime.format(formatter);
    }


    private Long generateBookingCode() {
        // Lấy thời gian hiện tại tính theo mili-giây
        long timestamp = System.currentTimeMillis(); // VD: 1716638490123

        // Sinh một số ngẫu nhiên 3 chữ số (000–999)
        int random = (int) (Math.random() * 1000); // VD: 345

        // Ghép timestamp và random thành chuỗi
        String codeStr = String.format("%d%03d", timestamp, random); // VD: "1716638490123345"

        // Chuyển về Long
        return Long.parseLong(codeStr);
    }

    private BookingEntity getBooking(Long bookingCode) {
        if (bookingCode == null) {
            throw new BusinessException("404", "bookingCode must be not null");
        }
        return bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException("404", "Booking not found with bookingCode: " + bookingCode));
    }

    private ShowtimeEntity getShowtime(Long id) {
        if (id == null) {
            throw new BusinessException("404", "id must be not null");
        }
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("404", "Showtime not found with id: " + id));

    }

    private AccountEntity getAccount(Long id) {
        if (id == null) {
            throw new BusinessException("404", "id must be not null");
        }
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("404", "Account not found with id: " + id));

    }
}