package com.example.chatserverparticipant.global.redis;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.repository.SseEmitterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisMessageListenerService implements MessageListener {

    private final SseEmitterRepository sseEmitterRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.info("채널 확인: {}", channel);
        log.info("날 것 그대로의 메세지: " + body);

        try {
            // JSON 파싱
            List<UserReadDTO> dto = new ObjectMapper().readValue(body, new TypeReference<List<UserReadDTO>>() {});
            log.info("파싱: {}", dto.toString());

            // 해당 채팅방의 SSE Emitter 찾기
            Optional<SseEmitter> optionalEmitter = sseEmitterRepository.findById(channel);
            optionalEmitter.ifPresent(emitter -> {
                try {
                    // 이벤트 타입 및 ID를 명시적으로 설정
                    emitter.send(SseEmitter.event()
                            .id(channel.replaceFirst("chat_", "")) // 이벤트 ID 설정
//                            .name("onMessage") // 이벤트 타입 설정(리액트랑 동일하게 맞춰야 함)
                            .data(dto)); // SSE Emitter로 데이터 전송
                } catch (IOException e) {
                    log.error("SSE 전송 오류: {}", e.getMessage());
                    sseEmitterRepository.deleteById(channel); // 오류 발생 시 emitter 제거
                } catch (IllegalStateException e) {
                    log.error("SSE 상태 오류: {}", e.getMessage());
                    sseEmitterRepository.deleteById(channel); // 상태 오류 발생 시 제거
                }
            });
        } catch (JsonProcessingException e) {
            log.error("메시지 파싱 오류: {}", e.getMessage());
        }
    }

}
