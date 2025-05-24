package com.cinema.booking_app.showtime.service.impl;

import com.cinema.booking_app.common.base.dto.response.BaseDto;
import com.cinema.booking_app.common.enums.SeatStatus;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.config.SeatWebSocketHandler;
import com.cinema.booking_app.room.entity.RowEntity;
import com.cinema.booking_app.room.entity.SeatEntity;
import com.cinema.booking_app.room.repository.RowRepository;
import com.cinema.booking_app.room.repository.SeatRepository;
import com.cinema.booking_app.showtime.dto.response.SeatDto;
import com.cinema.booking_app.showtime.dto.response.SeatsDto;
import com.cinema.booking_app.showtime.entity.SeatShowtimeEntity;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.SeatShowtimeRepository;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.showtime.service.SeatShowtimeService;
import com.cinema.booking_app.user.entity.AccountEntity;
import com.cinema.booking_app.user.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatShowtimeServiceImpl implements SeatShowtimeService {

    private static final int MAX_SEATS = 8;
    ShowtimeRepository showtimeRepository;
    SeatShowtimeRepository seatShowtimeRepository;
    SeatRepository seatRepository;
    RowRepository rowRepository;
    AccountRepository accountRepository;
    SeatWebSocketHandler seatWebSocketHandler;

    @Override
    @Transactional
    public BaseDto holdSeat(Long userId, Long seatId, Long showtimeId) {
        validateIds(userId, seatId, showtimeId);

        // Check max seats constraint
        if (seatShowtimeRepository.countBookedSeatsByUserAndShowtime(userId, showtimeId) > MAX_SEATS) {
            throw new BusinessException("400", "Maximum booked seats reached: " + MAX_SEATS);
        }

        // Fetch entities
        AccountEntity account = getAccount(userId);
        SeatEntity seat = getSeat(seatId);
        ShowtimeEntity showtime = getShowtime(showtimeId);

        // Create and save seat hold
        SeatShowtimeEntity entity = SeatShowtimeEntity.builder()
                .user(account)
                .showtime(showtime)
                .seat(seat)
                .status(SeatStatus.HOLD)
                .canceledTime(Instant.now().plus(10, ChronoUnit.MINUTES))
                .build();

        try {
            SeatShowtimeEntity saved = seatShowtimeRepository.save(entity);
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
        List<SeatShowtimeEntity> seatShowtimeList = seatShowtimeRepository.findAllById(seatShowtimeIds);
        if (seatShowtimeList.isEmpty()) {
            throw new BusinessException("404", "No seat showtimes found for provided IDs");
        }

        Instant newCancelTime = Instant.now().plus(10, ChronoUnit.MINUTES);
        seatShowtimeList.forEach(seat -> seat.setCanceledTime(newCancelTime));

        List<SeatShowtimeEntity> saved = seatShowtimeRepository.saveAll(seatShowtimeList);

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

        seatShowtimeRepository.deleteAllByIdInBatch(seatShowtimeIds);
        broadcastSeatUpdate(showtimeId, new ArrayList<>());
    }

    @Override
    public List<SeatsDto> getAllByShowtimeId(Long showtimeId) {
        validateIds(null, null, showtimeId);
        ShowtimeEntity showtime = getShowtime(showtimeId);

        // Fetch seat showtimes with join to reduce queries
        List<SeatShowtimeEntity> seatShowtimes = seatShowtimeRepository.findByShowtimeId(showtimeId);
        Map<Long, SeatShowtimeEntity> seatShowtimeMap = seatShowtimes.stream()
                .collect(Collectors.toMap(
                        sb -> sb.getSeat().getId(),
                        Function.identity(),
                        (e1, e2) -> e1 // Handle potential duplicates
                ));

        // Fetch rows and seats in one query
        List<RowEntity> rowEntities = rowRepository.findByRoomId(showtime.getRoom().getId());
        List<SeatsDto> seatsDtos = new ArrayList<>(rowEntities.size());

        for (RowEntity row : rowEntities) {
            List<SeatDto> seatDtos = row.getSeats().stream()
                    .map(seat -> {
                        SeatShowtimeEntity seatShowtime = seatShowtimeMap.getOrDefault(seat.getId(), null);
                        return SeatDto.builder()
                                .seatShowtimeId(seatShowtime != null ? seatShowtime.getId() : null)
                                .seatId(seat.getId())
                                .seatName(seat.getRow().getLabel() + seat.getSeatNumber())
                                .status(seatShowtime != null ? seatShowtime.getStatus() : SeatStatus.AVAILABLE)
                                .selectedByUserId(seatShowtime != null && seatShowtime.getUser() != null
                                        ? seatShowtime.getUser().getId() : -1L)
                                .build();
                    })
                    .toList();

            seatsDtos.add(SeatsDto.builder()
                    .rowId(row.getId())
                    .seats(seatDtos)
                    .build());
        }

        return seatsDtos;
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void releaseExpiredHeldSeats() {
        List<SeatShowtimeEntity> expiredSeats = seatShowtimeRepository.findExpiredSeats(Instant.now());
        if (expiredSeats.isEmpty()) {
            return;
        }

        Map<Long, List<SeatShowtimeEntity>> seatsByShowtime = expiredSeats.stream()
                .collect(Collectors.groupingBy(seat -> seat.getShowtime().getId()));

        seatShowtimeRepository.deleteAllInBatch(expiredSeats);

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

    private void broadcastSeatUpdate(Long showtimeId, List<SeatShowtimeEntity> entity) {
        try {
            seatWebSocketHandler.broadcastSeatUpdate(showtimeId.toString(), entity);
        } catch (IOException e) {
            log.error("Failed to broadcast seat update for showtimeId {}: {}", showtimeId, e.getMessage());
            throw new BusinessException("500", "WebSocket broadcast failed: " + e.getMessage());
        }
    }
}
