package com.cinema.booking_app.showtime.service.impl;

import com.cinema.booking_app.booking.repository.BookingRepository;
import com.cinema.booking_app.common.enums.MovieStatus;
import com.cinema.booking_app.common.error.BusinessException;
import com.cinema.booking_app.movie.entity.MovieEntity;
import com.cinema.booking_app.movie.repository.MovieRepository;
import com.cinema.booking_app.room.entity.RoomEntity;
import com.cinema.booking_app.room.repository.RoomRepository;
import com.cinema.booking_app.showtime.dto.request.ShowtimeRequestDto;
import com.cinema.booking_app.showtime.dto.response.ShowtimeResponseDto;
import com.cinema.booking_app.showtime.entity.ShowtimeEntity;
import com.cinema.booking_app.showtime.repository.ShowtimeRepository;
import com.cinema.booking_app.showtime.repository.TicketPriceRuleRepository;
import com.cinema.booking_app.showtime.service.ShowtimeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ShowtimeServiceImpl implements ShowtimeService {

    ShowtimeRepository showtimeRepository;
    MovieRepository movieRepository;
    RoomRepository roomRepository;
    TicketPriceRuleRepository ticketPriceRuleRepository;
    BookingRepository bookingRepository;

    @Override
    public ShowtimeResponseDto create(ShowtimeRequestDto dto) {
        MovieEntity movie = movieRepository.findById(dto.getMovieId()).orElseThrow(() -> new BusinessException("404", "Không tìm thấy phim với id " + dto.getMovieId()));
        RoomEntity room = roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new BusinessException("404", "Không tìm thấy phòng chiếu với id " + dto.getRoomId()));
        LocalTime endTime = dto.getStartTime().plusMinutes(movie.getDuration() == null ? 120 : movie.getDuration());
        if (showtimeRepository.isOverlappingShowtime(dto.getRoomId(), dto.getShowDate(), dto.getStartTime(), endTime)) {
            throw new BusinessException("400", "Đã có suất chiếu khác trong khoảng thời gian này.");
        }
        DayOfWeek day = dto.getShowDate().getDayOfWeek();
        boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
        boolean isEvening = dto.getStartTime().isAfter(LocalTime.of(17, 0));
        Optional<BigDecimal> ticketPriceByRule = ticketPriceRuleRepository.findTicketPriceByRule(isWeekend, isEvening);
        ShowtimeEntity showtime = ShowtimeEntity.builder()
                .movie(movie)
                .room(room)
                .showDate(dto.getShowDate())
                .startTime(dto.getStartTime())
                .endTime(endTime)
                .ticketPrice(ticketPriceByRule.orElse(BigDecimal.valueOf(45000)))
                .isActive(true)
                .build();

        ShowtimeEntity saved = showtimeRepository.save(showtime);
        movie.setStatus(MovieStatus.NOW_SHOWING);
        return mapToDto(saved);
    }

    @Override
    public List<ShowtimeResponseDto> getAll() {
        return showtimeRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ShowtimeResponseDto> findByCinemaIdAndShowDate(Long cinemaId, LocalDate showDate) {
        return showtimeRepository.findByCinemaIdAndShowDate(cinemaId, showDate).stream().map(this::mapToDto).toList();
    }

    @Override
    public ShowtimeResponseDto getById(Long id) {
        return showtimeRepository.findById(id).map(this::mapToDto).orElseThrow(() -> new BusinessException("404", "Không tìm thấy lịch chiếu có id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (bookingRepository.existsByShowtimeId(id))
            throw new BusinessException("400", "Suất chiếu này đã có người book không thể xóa");
        showtimeRepository.deleteById(id);
    }

    @Override
    public List<ShowtimeResponseDto> getByMovieIdAndCinemaId(Long movieId, Long cinemaId) {
        return showtimeRepository.findByMovieIdAndCinemaId(movieId, cinemaId, LocalDate.now(), LocalTime.now()).stream()
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
                .endTime(entity.getEndTime())
                .ticketPrice(entity.getTicketPrice())
                .build();
    }
}