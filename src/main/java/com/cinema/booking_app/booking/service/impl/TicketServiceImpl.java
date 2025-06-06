package com.cinema.booking_app.booking.service.impl;

import com.cinema.booking_app.booking.dto.response.SeatDto;
import com.cinema.booking_app.booking.dto.response.SeatsDto;
import com.cinema.booking_app.booking.entity.TicketEntity;
import com.cinema.booking_app.booking.repository.TicketRepository;
import com.cinema.booking_app.booking.service.TicketService;
import com.cinema.booking_app.common.base.dto.response.BaseDto;
import com.cinema.booking_app.common.enums.SeatStatus;
import com.cinema.booking_app.common.enums.SeatType;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.config.SeatWebSocketHandler;
import com.cinema.booking_app.room.entity.RowEntity;
import com.cinema.booking_app.room.entity.SeatEntity;
import com.cinema.booking_app.room.repository.RowRepository;
import com.cinema.booking_app.room.repository.SeatRepository;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketServiceImpl implements TicketService {

    private static final int MAX_SEATS = 8;
    ShowtimeRepository showtimeRepository;
    TicketRepository ticketRepository;
    SeatRepository seatRepository;
    RowRepository rowRepository;
    AccountRepository accountRepository;
    SeatWebSocketHandler seatWebSocketHandler;

    @Override
    @Transactional
    public BaseDto holdSeat(Long userId, Long seatId, Long showtimeId, BigDecimal ticketPrice) {
        validateIds(userId, seatId, showtimeId);

        // Check max seats constraint
        if (ticketRepository.countBookedSeatsByUserAndShowtime(userId, showtimeId) > MAX_SEATS) {
            throw new BusinessException("400", "Maximum booked seats reached: " + MAX_SEATS);
        }

        // Fetch entities
        AccountEntity account = getAccount(userId);
        SeatEntity seat = getSeat(seatId);
        ShowtimeEntity showtime = getShowtime(showtimeId);

        if (ticketPrice == null || ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("400", "Ticket price must be greater than or equal to zero");
        }

        // Create and save seat hold
        TicketEntity entity = TicketEntity.builder()
                .user(account)
                .showtime(showtime)
                .seat(seat)
                .status(SeatStatus.HOLD)
                .canceledTime(Instant.now().plus(10, ChronoUnit.MINUTES))
                .ticketPrice(ticketPrice)
                .build();

        try {
            TicketEntity saved = ticketRepository.save(entity);
            broadcastSeatUpdate(showtimeId, List.of(saved));
            return BaseDto.builder().id(saved.getId()).build();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("409", "Seat already held or booked for this showtime");
        }
    }

    @Override
    @Transactional
    public List<BaseDto> extendSeatHoldTime(Set<Long> seatShowtimeIds, Long showtimeId) {
        validateIds(null, null, showtimeId);
        if (seatShowtimeIds == null || seatShowtimeIds.isEmpty()) {
            throw new BusinessException("400", "Vui lòng chọn ghế");
        }

        // Fetch and update seat hold times in batch
        List<TicketEntity> seatShowtimeList = ticketRepository.findAllById(seatShowtimeIds);
        if (seatShowtimeList.isEmpty()) {
            throw new BusinessException("404", "No seat showtimes found for provided IDs");
        }

        Instant newCancelTime = Instant.now().plus(10, ChronoUnit.MINUTES);
        seatShowtimeList.forEach(seat -> seat.setCanceledTime(newCancelTime));

        List<TicketEntity> saved = ticketRepository.saveAll(seatShowtimeList);

        return saved.stream()
                .map(s -> BaseDto.builder().id(s.getId()).build())
                .toList();
    }

    @Override
    @Transactional
    public void releaseSeatByIds(List<Long> seatShowtimeIds, Long showtimeId) {
        validateIds(null, null, showtimeId);
        if (seatShowtimeIds == null || seatShowtimeIds.isEmpty()) {
            throw new BusinessException("400", "SeatShowtime IDs cannot be null or empty");
        }

        ticketRepository.deleteAllByIdInBatch(seatShowtimeIds);
        broadcastSeatUpdate(showtimeId, new ArrayList<>());
    }

    @Override
    public List<SeatsDto> getAllByShowtimeId(Long showtimeId) {
        log.info("User đang chọn ghế: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        validateIds(null, null, showtimeId);
        ShowtimeEntity showtime = getShowtime(showtimeId);
        BigDecimal basePrice = showtime.getTicketPrice();

        List<TicketEntity> seatShowtimes = ticketRepository.findByShowtimeId(showtimeId);
        Map<Long, TicketEntity> seatShowtimeMap = mapSeatIdToTicket(seatShowtimes);

        List<RowEntity> rowEntities = rowRepository.findByRoomId(showtime.getRoom().getId());
        List<SeatsDto> seatsDtos = new ArrayList<>();

        for (RowEntity row : rowEntities) {
            seatsDtos.add(buildSeatsDto(row, seatShowtimeMap, basePrice));
        }

        return seatsDtos;
    }

    private Map<Long, TicketEntity> mapSeatIdToTicket(List<TicketEntity> tickets) {
        Map<Long, TicketEntity> map = new HashMap<>();
        for (TicketEntity ticket : tickets) {
            Long seatId = ticket.getSeat().getId();
            map.putIfAbsent(seatId, ticket);
        }
        return map;
    }

    private SeatsDto buildSeatsDto(RowEntity row, Map<Long, TicketEntity> seatShowtimeMap, BigDecimal basePrice) {
        List<SeatDto> seatDtos = new ArrayList<>();

        for (SeatEntity seat : row.getSeats()) {
            TicketEntity ticket = seatShowtimeMap.get(seat.getId());
            seatDtos.add(buildSeatDto(seat, ticket, row, basePrice));
        }

        return SeatsDto.builder()
                .rowId(row.getId())
                .seats(seatDtos)
                .build();
    }

    private SeatDto buildSeatDto(SeatEntity seat, TicketEntity ticket, RowEntity row, BigDecimal basePrice) {
        BigDecimal ticketPrice = basePrice;
        SeatType seatType = seat.getSeatType();
        if (ticket != null) {
            if (SeatType.VIP.equals(seatType))
                ticketPrice = ticketPrice.multiply(BigDecimal.valueOf(1.5));
            if (SeatType.COUPLE.equals(seatType))
                ticketPrice = ticketPrice.multiply(BigDecimal.valueOf(1.8));
        }

        return SeatDto.builder()
                .seatShowtimeId(ticket != null ? ticket.getId() : null)
                .seatId(seat.getId())
                .seatName(row.getLabel() + seat.getSeatNumber())
                .status(ticket != null ? ticket.getStatus() : SeatStatus.AVAILABLE)
                .selectedByUserId(ticket != null && ticket.getUser() != null ? ticket.getUser().getId() : -1L)
                .seatType(seatType)
                .price(ticketPrice)
                .build();
    }


    @Scheduled(fixedRate = 30000)
    @Transactional
    public void releaseExpiredHeldSeats() {
        List<TicketEntity> expiredSeats = ticketRepository.findExpiredSeats(Instant.now());
        if (expiredSeats.isEmpty()) {
            return;
        }

        Map<Long, List<TicketEntity>> seatsByShowtime = expiredSeats.stream()
                .collect(Collectors.groupingBy(seat -> seat.getShowtime().getId()));

        ticketRepository.deleteAllInBatch(expiredSeats);

        seatsByShowtime.keySet().forEach(showtimeId -> broadcastSeatUpdate(showtimeId, null));

        log.info("Released {} expired held seats", expiredSeats.size());
    }

    // Helper methods
    private void validateIds(Long userId, Long seatId, Long showtimeId) {
        if (userId != null && userId <= 0) {
            throw new BusinessException("400", "Invalid user ID");
        }
        if (seatId != null && seatId <= 0) {
            throw new BusinessException("400", "Invalid seat ID");
        }
        if (showtimeId == null || showtimeId <= 0) {
            throw new BusinessException("400", "Invalid showtime ID");
        }
    }

    private AccountEntity getAccount(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("404", "Account not found for ID: " + userId));
    }

    private SeatEntity getSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new BusinessException("404", "Seat not found for ID: " + seatId));
    }

    private ShowtimeEntity getShowtime(Long showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new BusinessException("404", "Showtime not found for ID: " + showtimeId));
    }

    private void broadcastSeatUpdate(Long showtimeId, List<TicketEntity> entity) {
        try {
            seatWebSocketHandler.broadcastSeatUpdate(showtimeId.toString(), entity);
        } catch (IOException e) {
            log.error("Failed to broadcast seat update for showtimeId {}: {}", showtimeId, e.getMessage());
            throw new BusinessException("500", "WebSocket broadcast failed: " + e.getMessage());
        }
    }
}
