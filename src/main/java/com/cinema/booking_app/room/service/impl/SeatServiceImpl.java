package com.cinema.booking_app.room.service.impl;

import com.cinema.booking_app.common.enums.SeatType;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.config.SeatWebSocketHandler;
import com.cinema.booking_app.room.dto.request.create.SeatRequestDto;
import com.cinema.booking_app.room.dto.request.update.SeatUpdateDto;
import com.cinema.booking_app.room.dto.response.SeatResponseDto;
import com.cinema.booking_app.room.entity.RowEntity;
import com.cinema.booking_app.room.entity.SeatEntity;
import com.cinema.booking_app.room.mapper.SeatMapper;
import com.cinema.booking_app.room.repository.RowRepository;
import com.cinema.booking_app.room.repository.SeatRepository;
import com.cinema.booking_app.room.service.SeatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SeatServiceImpl implements SeatService {
    RowRepository rowRepository;
    SeatRepository seatRepository;
    SeatMapper seatMapper;
    SeatWebSocketHandler seatWebSocketHandler;

    @Override
    public SeatResponseDto create(SeatRequestDto dto) {
        RowEntity row = existsRow(dto.getRowId());
        validateDuplicateName(dto.getSeatNumber(), row.getId());

        SeatEntity seat = seatMapper.toEntity(dto);
        seat.setRow(row);

        return saveAndReturnDto(seat);
    }

    @Override
    public SeatResponseDto update(Long id, SeatUpdateDto dto) {
        SeatEntity seat = existsSeat(id);
        String updateSeatNumber = dto.getSeatNumber() != null ? dto.getSeatNumber() : seat.getSeatNumber();
        Long updateRowId = dto.getRowId() != null ? dto.getRowId() : seat.getRow().getId();
        SeatType updateSeatType = dto.getSeatType() != null ? dto.getSeatType() : seat.getSeatType();

        seat.setSeatNumber(updateSeatNumber);
        seat.setSeatType(updateSeatType);
        seat.setRow(existsRow(updateRowId));

        return saveAndReturnDto(seat);
    }

    @Override
    public void delete(Long id) {
        seatRepository.deleteById(id);
    }

    @Override
    public SeatResponseDto getById(Long id) {
        return seatMapper.toDto(existsSeat(id));
    }

    @Override
    public List<SeatResponseDto> getAll() {
        return seatMapper.toDto(seatRepository.findAll());
    }

    @Override
    public void holdSeat(Long userId, Long seatId) {
        if (seatId == null || userId == null)
            return;
        SeatEntity seat = existsSeat(seatId);

        seat.setIsHeld(true);
        seat.setHeldUntil(OffsetDateTime.now().plusMinutes(10));
        seat.setSelectedByUserId(userId);
        seat.setSelected(true);
        seatRepository.save(seat);
        try {
            seatWebSocketHandler.broadcastSeatUpdate(seat.getRow().getRoom().getId().toString(), seat);
        } catch (IOException e) {
            throw new BusinessException("500", e.getMessage());
        }

    }

    @Override
    public void releaseSeatById(List<Long> seatIds) {
        if (seatIds.isEmpty())
            return;
        List<SeatEntity> expiredSeats = seatRepository.findAllById(seatIds);
        for (SeatEntity seat : expiredSeats) {
            seat.setIsHeld(false);
            seat.setHeldUntil(null);
            seat.setSelectedByUserId(null);
            seat.setSelected(false);
            seatRepository.save(seat);
            try {
                seatWebSocketHandler.broadcastSeatUpdate(seat.getRow().getRoom().getId().toString(), seat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredSeats() {
        List<SeatEntity> expiredSeats = seatRepository.findByIsHeldTrueAndHeldUntilBefore(OffsetDateTime.now());
        if (expiredSeats.isEmpty())
            return;
        for (SeatEntity seat : expiredSeats) {
            seat.setIsHeld(false);
            seat.setHeldUntil(null);
            seat.setSelectedByUserId(null);
            seat.setSelected(false);
            seatRepository.save(seat);
            try {
                seatWebSocketHandler.broadcastSeatUpdate(seat.getRow().getRoom().getId().toString(), seat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private SeatEntity existsSeat(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy ghế với id: " + id));
    }

    private RowEntity existsRow(Long id) {
        return rowRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy hàng ghế với id: " + id));
    }

    private void validateDuplicateName(String seatNumber, Long rowId) {
        if (seatRepository.existsBySeatNumberAndRowId(seatNumber, rowId)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Hàng ghế đã tồn tại ghế có tên: " + seatNumber);
        }
    }

    private SeatResponseDto saveAndReturnDto(SeatEntity seat) {
        return seatMapper.toDto(seatRepository.save(seat));
    }
}