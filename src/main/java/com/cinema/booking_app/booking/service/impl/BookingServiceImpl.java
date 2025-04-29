package com.cinema.booking_app.booking.service.impl;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.booking.entity.TicketEntity;
import com.cinema.booking_app.booking.mapper.BookingMapper;
import com.cinema.booking_app.booking.repository.BookingRepository;
import com.cinema.booking_app.booking.repository.TicketRepository;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.service.impl.MailService;
import com.cinema.booking_app.common.base.service.impl.QRCodeService;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.room.entity.SeatEntity;
import com.cinema.booking_app.room.repository.SeatRepository;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    TicketRepository ticketRepository;
    ShowtimeRepository showtimeRepository;
    AccountRepository accountRepository;
    SeatRepository seatRepository;
    BookingMapper bookingMapper;
    QRCodeService qrCodeService;
    MailService mailService;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) {
        // Kiểm tra user và showtime
        AccountEntity user = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("404", "Không tìm thấy người dùng"));
        ShowtimeEntity showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new BusinessException("404", "Không tìm thấy lịch chiếu"));

        // Kiểm tra ghế trống
        List<Long> bookedSeatIds = ticketRepository.findByShowtimeId(request.getShowtimeId())
                .stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();
        List<Long> requestedSeatIds = request.getSeatIds();
        if (bookedSeatIds.stream().anyMatch(requestedSeatIds::contains)) {
            throw new BusinessException("400", "Ghế đã được đặt trước đó");
        }

        // Kiểm tra tổng giá
        BigDecimal ticketPrice = showtime.getTicketPrice();
        BigDecimal expectedTotalPrice = ticketPrice.multiply(BigDecimal.valueOf(requestedSeatIds.size()));

        if (expectedTotalPrice.compareTo(request.getTotalPrice()) != 0) {
            throw new IllegalArgumentException("Total price mismatch: expected " + expectedTotalPrice);
        }

        // Tạo booking
        BookingEntity booking = BookingEntity.builder()
                .bookingCode(generateBookingCode())
                .bookingTime(java.time.LocalDateTime.now())
                .user(user)
                .showtime(showtime)
                .totalPrice(request.getTotalPrice())
                .paymentStatus("PENDING")
                .build();

        // Tạo tickets
        BookingEntity finalBooking = booking;
        Set<TicketEntity> tickets = requestedSeatIds.stream()
                .map(seatId -> {
                    SeatEntity seat = seatRepository.findById(seatId)
                            .orElseThrow(() -> new IllegalArgumentException("Seat not found"));
                    return TicketEntity.builder()
                            .booking(finalBooking)
                            .seat(seat)
                            .price(showtime.getTicketPrice())
                            .build();
                })
                .collect(Collectors.toSet());
        booking.setTickets(tickets);

        // Lưu booking
        booking = bookingRepository.save(booking);

        try {
            booking.setBookingUrl(qrCodeService.generateAndUploadQRCode(booking.getBookingCode()));
            mailService.sendTicketEmail("duongtuan10122003@gmail.com", booking.getBookingUrl());
        } catch (IOException e) {
            throw new BusinessException("400", "Có lỗi khi tạo mã qr và upload");
        }

        // Ánh xạ sang DTO
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto getBookingByCode(String bookingCode) {
        BookingEntity booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException("404", "Không tìm thấy đơn đặt vé nào"));
        return bookingMapper.toDto(booking);
    }

    private String generateBookingCode() {
        return "BK-" + System.currentTimeMillis();
    }
}