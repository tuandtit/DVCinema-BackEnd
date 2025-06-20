package com.cinema.booking_app.booking.service.impl;

import com.cinema.booking_app.booking.dto.request.BookingRequestDto;
import com.cinema.booking_app.booking.dto.response.BookingHistoryDto;
import com.cinema.booking_app.booking.dto.response.BookingResponseDto;
import com.cinema.booking_app.booking.dto.response.TicketDto;
import com.cinema.booking_app.booking.entity.BookingEntity;
import com.cinema.booking_app.booking.entity.TicketEntity;
import com.cinema.booking_app.booking.mapper.BookingMapper;
import com.cinema.booking_app.booking.repository.BookingRepository;
import com.cinema.booking_app.booking.repository.TicketRepository;
import com.cinema.booking_app.booking.service.BookingService;
import com.cinema.booking_app.common.base.service.impl.MailService;
import com.cinema.booking_app.common.enums.PaymentStatus;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    TicketRepository ticketRepository;
    BookingMapper bookingMapper;
    MailService mailService;
    AsyncQrService asyncQrService;
    ShowtimeRepository showtimeRepository;
    AccountRepository accountRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) {
        // Tạo booking
        Long bookingCode = generateBookingCode();
        BookingEntity booking;
        booking = bookingRepository.save(BookingEntity.builder()
                .bookingCode(bookingCode)
                .showtimeId(getShowtime(request.getShowtimeId()).getId())
                .accountId(getAccount(request.getAccountId()).getId())
                .totalPrice(request.getTotalPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .isUsed(false)
                .build());

        asyncQrService.generateAndUploadQRCodeAsync(bookingCode);

        ticketRepository.setBookingCode(bookingCode, request.getSeatShowtimeIds());

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
        ticketRepository.confirmBook(bookingCode);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingCode) {
        ticketRepository.deleteAllByBooking_BookingCode(bookingCode);
        bookingRepository.deleteByBookingCode(bookingCode);
    }

    @Transactional
    @Override
    public BookingResponseDto confirmAndGetBookingByCode(Long bookingCode) {
        BookingEntity booking = getBooking(bookingCode);
        if (booking.getPaymentStatus() != PaymentStatus.SUCCESS) {
            booking.setPaymentStatus(PaymentStatus.SUCCESS);
            booking.setBookingTime(LocalDateTime.now());
            ticketRepository.confirmBook(bookingCode);
            bookingRepository.save(booking);
        }

        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("mail gui den: {}", username);
        mailService.sendTicketEmail(username, booking.getBookingUrl());
        BookingResponseDto dto = bookingMapper.toDto(booking);
        ShowtimeEntity showtime = showtimeRepository.findById(booking.getShowtimeId())
                .orElseThrow(() -> new BusinessException("404", "Showtime not found with id: " + booking.getShowtimeId()));
        List<TicketEntity> seatShowtimeEntities = ticketRepository.findByBooking_BookingCode(bookingCode);
        String seatName = String.join(",",
                seatShowtimeEntities.stream()
                        .map(entity -> entity.getSeat().getRow().getLabel() + entity.getSeat().getSeatNumber())
                        .toList());
        dto.setCinemaName(showtime.getRoom().getCinema().getName());
        dto.setAddress(showtime.getRoom().getCinema().getAddress());
        dto.setMovieTitle(showtime.getMovie().getTitle());
        dto.setSeatName(seatName);
        dto.setRoomName(showtime.getRoom().getName());
        dto.setShowtime(getShowDateTimeFormatted(showtime.getShowDate(), showtime.getStartTime()));

        return dto;
    }

    @Override
    public List<TicketDto> checkin(Long bookingCode) {
        BookingEntity booking = getBooking(bookingCode);
        if (booking.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("400", "Đơn vé này chưa thanh toán thành công");
        }
        if (booking.isUsed())
            throw new BusinessException("400", "Đơn vé này đã được sử dụng");
        booking.setUsed(true);
        bookingRepository.save(booking);
        List<TicketDto> dtos = new ArrayList<>();
        List<TicketEntity> ticketEntities = ticketRepository.findByBooking_BookingCode(bookingCode);
        for (var entity : ticketEntities) {
            var showtime = entity.getShowtime();
            TicketDto dto = TicketDto.builder()
                    .cinemaName(showtime.getRoom().getCinema().getName())
                    .showtime(showtime.getMovie().getTitle())
                    .seatName(entity.getSeat().getRow().getLabel() + entity.getSeat().getSeatNumber())
                    .roomName(showtime.getRoom().getName())
                    .showtime(getShowDateTimeFormatted(showtime.getShowDate(), showtime.getStartTime()))
                    .movieTitle(showtime.getMovie().getTitle())
                    .price(entity.getTicketPrice())
                    .address(showtime.getRoom().getCinema().getAddress())
                    .build();
            dtos.add(dto);
        }

        return !dtos.isEmpty() ? dtos : List.of(TicketDto.builder().build());
    }

    @Override
    public List<BookingHistoryDto> getHistoryBooking(Long userId) {
        var bookingEntities = bookingRepository.findByAccountIdAndPaymentStatus(userId, PaymentStatus.SUCCESS);
        if (bookingEntities.isEmpty())
            return List.of();
        List<BookingHistoryDto> dtos = new ArrayList<>();
        for (var entity : bookingEntities) {
            List<TicketEntity> ticketEntities = ticketRepository.findByBooking_BookingCode(entity.getBookingCode());
            var showtime = getShowtime(entity.getShowtimeId());
            String cinemaName = showtime.getRoom().getCinema().getName() + "-" + showtime.getRoom().getName();
            String seatName = String.join(",",
                    ticketEntities.stream()
                            .map(ticket -> ticket.getSeat().getRow().getLabel() + ticket.getSeat().getSeatNumber())
                            .toList());
            BookingHistoryDto dto = BookingHistoryDto.builder()
                    .bookingCode(entity.getBookingCode().toString())
                    .cinemaName(cinemaName)
                    .movieName(showtime.getMovie().getTitle())
                    .seat(seatName)
                    .totalAmount(entity.getTotalPrice())
                    .bookingUrl(entity.getBookingUrl())
                    .bookingDate(this.formatBookingTime(entity.getBookingTime()))
                    .showDateTime(this.getShowDateTimeFormatted(showtime.getShowDate(), showtime.getStartTime()))
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    private String getShowDateTimeFormatted(LocalDate showDate, LocalTime startTime) {
        if (showDate == null || startTime == null) {
            return null; // hoặc throw exception nếu cần
        }
        LocalDateTime dateTime = LocalDateTime.of(showDate, startTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm");
        return dateTime.format(formatter);
    }

    public String formatBookingTime(LocalDateTime bookingTime) {
        if (bookingTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm");
        return bookingTime.format(formatter);
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