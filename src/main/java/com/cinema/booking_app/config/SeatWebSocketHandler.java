package com.cinema.booking_app.config;

import com.cinema.booking_app.room.entity.SeatEntity;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SeatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> sessionsByRoom = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SeatWebSocketHandler() {
        // Đăng ký module JavaTimeModule để hỗ trợ Java 8 date/time
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String showtimeId = getShowtimeIdFromSession(session);
        if (showtimeId == null) {
            session.close(CloseStatus.BAD_DATA.withReason("showtimeId is required"));
            return;
        }
        sessionsByRoom.computeIfAbsent(showtimeId.trim(), k -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("Client connected to room: " + showtimeId + "session: " + session.getId() + ", total sessions: " + sessionsByRoom.get(showtimeId).size());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        // Có thể xử lý tin nhắn từ client nếu cần
    }

    public void broadcastSeatUpdate(String showtimeId, SeatEntity seat) throws IOException {
        System.out.println("sessionsByRoom: " + sessionsByRoom);
        String seatUpdate = objectMapper.writeValueAsString(seat);
        CopyOnWriteArrayList<WebSocketSession> sessions = sessionsByRoom.get(showtimeId.trim());

        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(seatUpdate));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        String showtimeId = getShowtimeIdFromSession(session);
        if (showtimeId != null) {
            CopyOnWriteArrayList<WebSocketSession> sessions = sessionsByRoom.get(showtimeId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionsByRoom.remove(showtimeId);
                }
            }
            System.out.println("Client disconnected from room: " + showtimeId);
        }
    }

    private String getShowtimeIdFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return null;
        }
        String query = uri.getQuery();
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "showtimeId".equals(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }
}