package com.example.chatserverparticipant.domain.service;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.repository.SseEmitterRepository;
import com.example.chatserverparticipant.global.redis.RedisSubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "SSE_Service")
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final SseEmitterRepository sseEmitterRepository;
    private final RedisSubscribeService redisSubscribeService;
    private final RedisTemplate<String, Boolean> participatedTemplate;

    public SseEmitter subscribe(String memberKey) {
        redisSubscribeService.subscribe("chat_" + memberKey);
        List<UserReadDTO> data = this.getInitialData(memberKey);

        /**
         * 타임아웃 내에 데이터 입력이 없을 때를 대비한 로직 필요
         */
        SseEmitter sseEmitter = new SseEmitter(3000_000L); // emitter 생성 -> 이 타임아웃 내에 데이터 입력이 없으면 종료해버리는듯?
        sseEmitterRepository.save(memberKey, sseEmitter);

        // emitter handling
        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteById(memberKey));

        // dummy data 전송
        log.info("더미데이터 저장, 구독 시점");
        send(data, memberKey, sseEmitter);
        return sseEmitter;
    }

    private void send(Object data, String emitterKey, SseEmitter sseEmitter) {
        try {
            log.info("send to client {}:[{}]", emitterKey, data);
            // 이벤트 데이터 전송
            sseEmitter.send(SseEmitter.event()
                    .id(emitterKey)
                    .data(data, MediaType.APPLICATION_JSON)); // data가 메시지만 포함된다면 타입을 지정해줄 필요는 없다.
        } catch (IOException | IllegalStateException e) {
            log.error("IOException | IllegalStateException is occurred. ", e);
            sseEmitterRepository.deleteById(emitterKey);
        }
    }

    public List<UserReadDTO> getInitialData(String chatId) {
        Map<Object, Object> entries =
                participatedTemplate.opsForHash().entries("PARTICIPATED:" + chatId);

        // UserReadDTO 리스트 생성 및 초기 데이터 활용
        return entries.entrySet().stream()
                .map(e -> new UserReadDTO((String) e.getKey(), (Boolean) e.getValue()))
                .toList();
    }
}
