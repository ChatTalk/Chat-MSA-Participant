package com.example.chatserverparticipant.domain.repository;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, List<UserReadDTO>> userMessages = new ConcurrentHashMap<>(); // 사용자 메시지 저장

    public SseEmitter save(String eventId, SseEmitter sseEmitter) {
        emitters.put("chat_" + eventId, sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> findById(String memberId) {
        return Optional.ofNullable(emitters.get(memberId));
    }

    public void deleteById(String eventId) {
        emitters.remove(eventId);
    }

    public void saveUserMessage(String memberId, UserReadDTO user) {
        userMessages.computeIfAbsent(memberId, k -> new ArrayList<>()).add(user);
    }

    public List<UserReadDTO> getUserMessages(String memberId) {
        return userMessages.getOrDefault(memberId, new ArrayList<>());
    }

    public void clearUserMessages(String memberId) {
        userMessages.remove(memberId);
    }
}
