package com.example.chatserverparticipant.domain.service;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "SSE_Service")
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribe(String memberKey, List<UserReadDTO> data) {
        SseEmitter sseEmitter = new SseEmitter(30_000L); // emitter 생성
        sseEmitterRepository.save(memberKey, sseEmitter);

        // emitter handling
        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError((e) -> sseEmitter.complete());
        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteById(memberKey));

        // dummy data 전송
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

}
