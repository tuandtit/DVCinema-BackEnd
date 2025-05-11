package com.cinema.booking_app.showtime.service.impl;

import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.movie.repository.MovieRepository;
import com.cinema.booking_app.room.entity.RoomEntity;
import com.cinema.booking_app.room.repository.RoomRepository;
import com.cinema.booking_app.showtime.dto.request.ShowtimeRequestDto;
import com.cinema.booking_app.showtime.dto.response.ShowtimeResponseDto;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.showtime.service.ShowtimeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ShowtimeServiceImpl implements ShowtimeService {

    ShowtimeRepository showtimeRepository;
    MovieRepository movieRepository;
    RoomRepository roomRepository;

    @Override
    public ShowtimeResponseDto create(ShowtimeRequestDto dto) {
        MovieEntity movie = movieRepository.findById(dto.getMovieId()).orElseThrow(() -> new BusinessException("404", "Không tìm thấy phim với id " + dto.getMovieId()));
        RoomEntity room = roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new BusinessException("404", "Không tìm thấy phòng chiếu với id " + dto.getRoomId()));

        ShowtimeEntity showtime = ShowtimeEntity.builder().movie(movie).room(room).showDate(dto.getShowDate()).startTime(dto.getShowTime()).ticketPrice(dto.getTicketPrice()).isActive(true).build();

        ShowtimeEntity saved = showtimeRepository.save(showtime);
        return mapToDto(saved);
    }

    @Override
    public List<ShowtimeResponseDto> getAll() {
        return showtimeRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public ShowtimeResponseDto getById(Long id) {
        return showtimeRepository.findById(id).map(this::mapToDto).orElseThrow(() -> new BusinessException("404", "Không tìm thấy lịch chiếu có id: " + id));
    }

    @Override
    public void delete(Long id) {
        showtimeRepository.deleteById(id);
    }

    @Override
    public List<ShowtimeResponseDto> getByMovieIdAndCinemaId(Long movieId, Long cinemaId) {
        return showtimeRepository.findByMovieIdAndCinemaId(movieId, cinemaId).stream()
                .map(this::mapToDto).toList();
    }

    private ShowtimeResponseDto mapToDto(ShowtimeEntity entity) {
        return ShowtimeResponseDto.builder()
                .id(entity.getId())
                .movieId(entity.getMovie().getId())
                .movieTitle(entity.getMovie().getTitle())
                .roomId(entity.getRoom().getId())
                .roomName(entity.getRoom().getName())
                .cinemaId(entity.getRoom().getCinema().getId())
                .cinemaName(entity.getRoom().getCinema().getName())
                .showDate(entity.getShowDate())
                .startTime(entity.getStartTime())
                .ticketPrice(entity.getTicketPrice())
                .build();
    }
}