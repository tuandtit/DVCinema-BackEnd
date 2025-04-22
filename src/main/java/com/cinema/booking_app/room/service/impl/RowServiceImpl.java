package com.cinema.booking_app.room.service.impl;

import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.room.dto.request.create.RowRequestDto;
import com.cinema.booking_app.room.dto.request.update.RowUpdateDto;
import com.cinema.booking_app.room.dto.response.RowResponseDto;
import com.cinema.booking_app.room.entity.RoomEntity;
import com.cinema.booking_app.room.entity.RowEntity;
import com.cinema.booking_app.room.mapper.RowMapper;
import com.cinema.booking_app.room.repository.RoomRepository;
import com.cinema.booking_app.room.repository.RowRepository;
import com.cinema.booking_app.room.service.RowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RowServiceImpl implements RowService {
    RowRepository rowRepository;
    RoomRepository roomRepository;
    RowMapper rowMapper;

    @Override
    public RowResponseDto create(RowRequestDto dto) {
        RoomEntity room = existsRoom(dto.getRoomId());
        validateDuplicateName(dto.getLabel(), room.getId());

        RowEntity row = rowMapper.toEntity(dto);
        row.setRoom(room);

        return saveAndReturnDto(row);
    }

    @Override
    public RowResponseDto update(Long id, RowUpdateDto dto) {
        RowEntity row = existsRow(id);

        String updateLabel = dto.getLabel() != null ? dto.getLabel() : row.getLabel();
        Long updatedRoomId = dto.getRoomId() != null ? dto.getRoomId() : row.getRoom().getId();

        row.setLabel(updateLabel);
        row.setRoom(existsRoom(updatedRoomId));

        return saveAndReturnDto(row);
    }

    @Override
    public void delete(Long id) {
        rowRepository.deleteById(id);
    }

    @Override
    public RowResponseDto getById(Long id) {
        return rowMapper.toDto(existsRow(id));
    }

    @Override
    public List<RowResponseDto> getAll() {
        return rowMapper.toDto(rowRepository.findAll());
    }

    private RoomEntity existsRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy phòng chiếu với id: " + id));
    }

    private RowEntity existsRow(Long id) {
        return rowRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "Không tìm thấy hàng ghế với id: " + id));
    }

    private void validateDuplicateName(String label, Long roomId) {
        if (rowRepository.existsByLabelAndRoomId(label, roomId)) {
            throw new BusinessException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Phòng chiếu đã tồn tại hàng ghế có tên: " + label);
        }
    }

    private RowResponseDto saveAndReturnDto(RowEntity row) {
        return rowMapper.toDto(rowRepository.save(row));
    }
}
