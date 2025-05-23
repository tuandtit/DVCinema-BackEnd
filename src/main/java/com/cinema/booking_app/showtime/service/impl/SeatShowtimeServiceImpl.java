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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatShowtimeServiceImpl implements SeatShowtimeService {

    ShowtimeRepository showtimeRepository;
    SeatShowtimeRepository seatShowtimeRepository;
    SeatRepository seatRepository;
    RowRepository rowRepository;
    AccountRepository accountRepository;
    SeatWebSocketHandler seatWebSocketHandler;

    @Override
    public BaseDto holdSeat(Long userId, Long seatId, Long showtimeId) {
        AccountEntity account = getAccount(userId);
        SeatEntity seat = getSeat(seatId);
        ShowtimeEntity showtime = getShowtime(showtimeId);

        SeatShowtimeEntity entity = SeatShowtimeEntity.builder()
                .user(account)
                .showtime(showtime)
                .status(SeatStatus.HOLD)
                .bookedTime(null)
                .seat(seat)
                .build();

        try {
            seatWebSocketHandler.broadcastSeatUpdate(showtimeId.toString(), entity);
        } catch (IOException e) {
            throw new BusinessException("500", e.getMessage());
        }

        return BaseDto.builder()
                .id(seatShowtimeRepository.save(entity).getId())
                .build();
    }

    @Override
    @Transactional
    public void releaseSeatByIds(Long showtimeId, List<Long> seatIds) {
        seatShowtimeRepository.deleteByShowtimeIdAndSeatIds(showtimeId, seatIds);
        try {
            seatWebSocketHandler.broadcastSeatUpdate(showtimeId.toString(), new SeatShowtimeEntity());
        } catch (IOException e) {
            throw new BusinessException("500", e.getMessage());
        }
    }

    @Override
    public List<SeatsDto> getAllByShowtimeId(Long showtimeId) {
        ShowtimeEntity showtime = getShowtime(showtimeId);
        Long roomId = showtime.getRoom().getId();

        List<SeatShowtimeEntity> seatShowtimeEntities = seatShowtimeRepository.findByShowtimeId(showtimeId);

        Map<Long, SeatShowtimeEntity> seatShowtimeMap = seatShowtimeEntities.stream()
                .collect(Collectors.toMap(
                        sb -> sb.getSeat().getId(),
                        Function.identity()
                ));

        List<SeatsDto> seatsDtos = new ArrayList<>();

        List<RowEntity> rowEntities = rowRepository.findByRoomId(roomId);
        for (var rowEntity : rowEntities) {

            List<SeatEntity> seatEntities = rowEntity.getSeats();


            List<SeatDto> list = new ArrayList<>();
            for (SeatEntity seat : seatEntities) {
                SeatShowtimeEntity seatShowtimeEntity = seatShowtimeMap.get(seat.getId());
                SeatStatus status = SeatStatus.AVAILABLE;
                Long selectedByUserId = null;
                if (seatShowtimeEntity != null) {
                    status = seatShowtimeEntity.getStatus();
                    if (seatShowtimeEntity.getUser() != null) {
                        selectedByUserId = seatShowtimeEntity.getUser().getId();
                    }
                }
                list.add(SeatDto.builder()
                        .seatId(seat.getId())
                        .seatName(seat.getRow().getLabel() + seat.getSeatNumber())
                        .status(status)
                        .selectedByUserId(selectedByUserId == null ? -1 : selectedByUserId)
                        .build());
            }
            seatsDtos.add(SeatsDto.builder()
                    .seats(list)
                    .rowId(rowEntity.getId())
                    .build());
        }

        return seatsDtos;
    }

    private SeatEntity getSeat(Long id) {
        if (id == null) {
            throw new BusinessException("400", "id must not be null");
        }
        return seatRepository.findById(id)
                .orElseThrow(() -> new BusinessException("404", "Seat not found with id: " + id));
    }

    private AccountEntity getAccount(Long id) {
        if (id == null) {
            throw new BusinessException("400", "id must not be null");
        }
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("404", "Account not found with id: " + id));
    }

    private ShowtimeEntity getShowtime(Long id) {
        if (id == null) {
            throw new BusinessException("400", "id must not be null");
        }
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("404", "Showtime not found with id: " + id));
    }
}
