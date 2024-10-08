package com.example.chatserverparticipant.domain.controller;

import com.example.chatserverparticipant.global.facade.DistributedLockFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j(topic = "WEBSOCKET_EVENT_LISTENING")
@Controller
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final DistributedLockFacade distributedLockFacade;

    // 갖고온다!
    // 예를 최대한 활용해서 redis onMessage 에서 연결됐는지
    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) throws HttpResponseException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination == null || !destination.startsWith("/sub/participant/")) {
            throw new HttpResponseException(
                    HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "Server Error, 구독 경로 확인 필요"
            );
        }

        String id = destination.replaceFirst("/sub/participant/", "");
        log.info("구독할 때의 경로 아이디 갖고오기: {}", id);

        boolean lockAcquired = distributedLockFacade.tryLock(id, 10, 60);

        if (lockAcquired) {
            log.info("구독 경로에 대한 락 설정 성공: {}", id);
        } else {
            log.warn("구독 경로에 대한 락 설정 실패: {}", id);
        }
    }

}
