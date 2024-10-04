package com.example.chatserverparticipant.domain.controller;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.repository.SseEmitterRepository;
import com.example.chatserverparticipant.domain.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j(topic = "SSE_Controller")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/participants")
public class SseController {

    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String chatId) {
        List<UserReadDTO> dummyData = Collections.emptyList(); // 초기 데이터로 빈 배열(503 방지)
        return sseEmitterService.subscribe(chatId, dummyData);
    }
}
