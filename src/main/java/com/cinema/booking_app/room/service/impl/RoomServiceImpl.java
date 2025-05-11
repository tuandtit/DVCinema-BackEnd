package com.cinema.booking_app.room.service.impl;

import com.cinema.booking_app.cinema.entity.CinemaEntity;
import com.cinema.booking_app.cinema.repository.CinemaRepository;
import com.cinema.booking_app.common.enums.SeatType;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.room.dto.request.create.RoomRequestDto;
import com.cinema.booking_app.room.dto.request.update.RoomUpdateDto;
import com.cinema.booking_app.room.dto.response.RoomResponseDto;
import com.cinema.booking_app.room.entity.RoomEntity;
import com.cinema.booking_app.room.entity.RowEntity;
import com.cinema.booking_app.room.entity.SeatEntity;
import com.cinema.booking_app.room.mapper.RoomMapper;
import com.cinema.booking_app.room.repository.RoomRepository;
import com.cinema.booking_app.room.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RoomServiceImpl implements RoomService {
    CinemaRepository cinemaRepository;
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    @Override
    public RoomResponseDto create(RoomRequestDto dto) {
        CinemaEntity cinema = existsCinema(dto.getCinemaId());
        validateDuplicateName(dto.getName(), cinema.getId());

        RoomEntity room = roomMapper.toEntity(dto);
        room.setCinema(cinema);

        List<RowEntity> rows = IntStream.range(0, dto.getTotalRows()) // Dùng IntStream để thay thế vòng for
                .mapToObj(i -> {
                    String rowLabel = String.valueOf((char) ('A' + i));
                    RowEntity row = RowEntity.builder().label(rowLabel).room(room).build();

                    List<SeatEntity> seats = IntStream.range(1, dto.getSeatsPerRow() + 1) // Dùng IntStream cho seats
                            .mapToObj(j -> SeatEntity.builder()
                                    .seatNumber(String.valueOf(j))
                                    .seatType(SeatType.SINGLE)
                                    .row(row)
                                    .build())
                            .toList();

                    row.setSeats(seats);
                    return row;
                })
                .toList();

        room.setRows(rows);
        room.setIsActive(true);

        return saveAndReturnDto(room);
    }

    @Override
    public RoomResponseDto update(Long id, RoomUpdateDto dto) {
        RoomEntity room = existsRoom(id);
        String updatedName = dto.getName() != null ? dto.getName() : room.getName();
        Long updatedCinemaId = dto.getCinemaId() != null ? dto.getCinemaId() : room.getCinema().getId();

        room.setName(updatedName);
        room.setCinema(existsCinema(updatedCinemaId));

        return saveAndReturnDto(room);
    }

    @Override
    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public RoomResponseDto getById(Long id) {
        RoomEntity room = existsRoom(id);
        return roomMapper.toDto(room);
    }

    @Override
    public List<RoomResponseDto> getAll() {
        return roomMapper.toDto(roomRepository.findAll());
    }

    private RoomEntity existsRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy phòng chiếu với id: " + id));
    }

    private CinemaEntity existsCinema(Long id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy rạp chiếu phim với id: " + id));
    }

    private void validateDuplicateName(String name, Long cinemaId) {
        if (roomRepository.existsByNameAndCinemaId(name, cinemaId)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Rạp đã tồn tại phòng chiếu có tên: " + name);
        }
    }

    private RoomResponseDto saveAndReturnDto(RoomEntity room) {
        return roomMapper.toDto(roomRepository.save(room));
    }
}
