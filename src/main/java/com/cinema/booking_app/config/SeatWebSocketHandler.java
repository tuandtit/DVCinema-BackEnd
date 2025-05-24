package com.cinema.booking_app.config;

import com.cinema.booking_app.showtime.entity.SeatShowtimeEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SeatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> sessionsByShowtime = new ConcurrentHashMap<>();
    private final Map<String, Object> sessionLocks = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SeatWebSocketHandler() {
        objectMapper.registerModule(new JavaTimeModule()); // Hỗ trợ Java 8 date/time
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String showtimeId = getShowtimeIdFromSession(session);
        if (showtimeId == null) {
            session.close(CloseStatus.BAD_DATA.withReason("showtimeId is required"));
            return;
        }

        sessionsByShowtime
                .computeIfAbsent(showtimeId.trim(), k -> new CopyOnWriteArrayList<>())
                .add(session);

        sessionLocks.put(session.getId(), new Object());

        System.out.printf("Client connected to room: %s | session: %s | total sessions: %d%n",
                showtimeId, session.getId(), sessionsByShowtime.get(showtimeId).size());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        // Xử lý tin nhắn từ client nếu cần
    }

    public void broadcastSeatUpdate(String showtimeId, List<SeatShowtimeEntity> seats) throws IOException {
        String seatUpdate = objectMapper.writeValueAsString(seats);
        CopyOnWriteArrayList<WebSocketSession> sessions = sessionsByShowtime.get(showtimeId.trim());

        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    Object lock = sessionLocks.get(session.getId());
                    if (lock != null) {
                        synchronized (lock) {
                            try {
                                session.sendMessage(new TextMessage(seatUpdate));
                            } catch (IOException e) {
                                System.err.printf("Failed to send message to session: %s%n", session.getId());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String showtimeId = getShowtimeIdFromSession(session);
        if (showtimeId != null) {
            CopyOnWriteArrayList<WebSocketSession> sessions = sessionsByShowtime.get(showtimeId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionsByShowtime.remove(showtimeId);
                }
            }

            sessionLocks.remove(session.getId());

            System.out.printf("Client disconnected from showtime: %s | session: %s%n", showtimeId, session.getId());
        }
    }

    private String getShowtimeIdFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;

        String query = uri.getQuery();
        if (query == null) return null;

        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "showtimeId".equals(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }
}
