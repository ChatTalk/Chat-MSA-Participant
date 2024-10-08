package com.example.chatserverparticipant.domain.controller;

import com.example.chatserverparticipant.domain.service.ClientSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j(topic = "WEBSOCKET_EVENT_LISTENING")
@Controller
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ClientSessionManager clientSessionManager;

    // 갖고온다!
    // 예를 최대한 활용해서 redis onMessage 에서 연결됐는지
    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String pathId = accessor.getDestination();
        log.info("구독할 때의 경로 아이디 갖고오기: {}", pathId);
    }

//    @EventListener
//    public void handleWebSocketEvent(ApplicationEvent event) {
//        extractChatIdFromSession(event);
//    }
//
//    private String extractChatIdFromSession(Object event) {
//
//        String id = "0";
//
//        // 현재 로그에 찍히고 있음
//        if (event instanceof SessionConnectEvent) {
//            id = ((SessionConnectEvent) event).getSource().toString();
//            log.info("연결(현재형) 이벤트 아이디?: {}", id);
//        }
//
//        // 현재 로그에 찍히는 중
//        if (event instanceof SessionConnectedEvent) {
//            id = ((SessionConnectedEvent) event).getSource().toString();
//            log.info("연결 이벤트(과거형) 아이디?: {}", id);
//        }
//
//        // 현재 로그에 찍히고 있음
//        // 즉 현재 이 종류의 이벤트가 발생하고 있다는 뜻?
//        if (event instanceof SessionSubscribeEvent) {
//            id = ((SessionSubscribeEvent) event).getSource().toString();
//            log.info("구독 이벤트(현재형) 아이디?: {}", id);
//        }
//
//        if (event instanceof SessionDisconnectEvent) {
//            id = ((SessionDisconnectEvent) event).getSessionId();
//            log.info("연결 끊길 때(현재형)의 아이디?: {}", id);
//        }
//
//        if (event instanceof SessionDisconnectEvent) {
//            id = ((SessionDisconnectEvent) event).getSessionId();
//            log.info("연결 끊길 때(과거형)의 아이디?: {}", id);
//        }
//
//        // chatId 추출 로직 구현
//        return id; // 임시
//    }
}
