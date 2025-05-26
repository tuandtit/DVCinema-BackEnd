package com.cinema.booking_app.booking.service.impl;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.booking.entity.BookingSeatShowtimeEntity;
import com.cinema.booking_app.booking.mapper.BookingMapper;
import com.cinema.booking_app.booking.repository.BookingRepository;
import com.cinema.booking_app.booking.repository.BookingSeatShowtimeRepository;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.service.impl.MailService;
import com.cinema.booking_app.common.base.service.impl.QRCodeService;
import com.cinema.booking_app.common.enums.PaymentStatus;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.showtime.entity.SeatShowtimeEntity;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.SeatShowtimeRepository;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    SeatShowtimeRepository seatShowtimeRepository;
    BookingSeatShowtimeRepository bookingSeatShowtimeRepository;
    BookingMapper bookingMapper;
    QRCodeService qrCodeService;
    MailService mailService;
    ShowtimeRepository showtimeRepository;

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
                    .showtimeId(request.getShowtimeId())
                    .totalPrice(request.getTotalPrice())
                    .paymentStatus(PaymentStatus.PENDING)
                    .isUsed(false)
                    .build());
        } catch (IOException e) {
            throw new BusinessException("400", "Có lỗi khi tạo mã qr và upload");
        }
        List<BookingSeatShowtimeEntity> entities = new ArrayList<>();
        for (var id : request.getSeatShowtimeIds()) {
            SeatShowtimeEntity seatShowtime = getSeatShowtime(id);
            entities.add(BookingSeatShowtimeEntity.builder()
                    .bookingCode(booking.getBookingCode())
                    .seatShowtimeId(seatShowtime.getId())
                    .build());
        }

        bookingSeatShowtimeRepository.saveAll(entities);


        // Ánh xạ sang DTO
        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingResponseDto confirmPayment(Long bookingCode) {
        BookingEntity booking = getBooking(bookingCode);
        if (booking.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return bookingMapper.toDto(booking);
        }
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setBookingTime(LocalDateTime.now());
        mailService.sendTicketEmail("duongtuan10122003@gmail.com", booking.getBookingUrl());
        List<Long> seatShowtimeIds = bookingSeatShowtimeRepository.findAllSeatShowtimeIdsByBookingCode(bookingCode);
        seatShowtimeRepository.confirmBook(seatShowtimeIds);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBooking(Long bookingCode) {
        bookingRepository.deleteByBookingCode(bookingCode);
    }

    @Override
    public BookingResponseDto getBookingByCode(Long bookingCode) {
        BookingEntity booking = getBooking(bookingCode);
        BookingResponseDto dto = bookingMapper.toDto(booking);
        ShowtimeEntity showtime = showtimeRepository.findById(booking.getShowtimeId())
                .orElseThrow(() -> new BusinessException("404", "Showtime not found with id: " + booking.getShowtimeId()));
        List<Long> allSeatShowtimeIdsByBookingCode = bookingSeatShowtimeRepository.findAllSeatShowtimeIdsByBookingCode(bookingCode);
        List<SeatShowtimeEntity> seatShowtimeEntities = seatShowtimeRepository.findAllById(allSeatShowtimeIdsByBookingCode);
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

    private SeatShowtimeEntity getSeatShowtime(Long id) {
        if (id == null) {
            throw new BusinessException("404", "id must be not null");
        }
        return seatShowtimeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("404", "SeatShowtime not found with id: " + id));
    }
}