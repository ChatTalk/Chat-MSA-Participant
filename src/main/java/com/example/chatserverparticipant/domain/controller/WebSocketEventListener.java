package com.example.chatserverparticipant.domain.controller;

import com.example.chatserverparticipant.domain.service.EventQueueService;
import com.example.chatserverparticipant.global.facade.DistributedLockFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j(topic = "WEBSOCKET_EVENT_LISTENING")
@Controller
@RequiredArgsConstructor
public class WebSocketEventListener {

//    private final DistributedLockFacade distributedLockFacade;
    private final EventQueueService eventQueueService;

    // 갖고온다!
    // 예를 최대한 활용해서 redis onMessage 에서 연결됐는지
    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) throws HttpResponseException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        if (destination == null || !destination.startsWith("/sub/participant/")) {
            throw new HttpResponseException(
                    HttpStatus.SC_SERVICE_UNAVAILABLE,
                    "Server Error, 구독 경로 확인 필요"
            );
        }

        log.info("구독 시점의 세션 아이디: {}", sessionId);

        String id = destination.replaceFirst("/sub/participant/", "");
        log.info("구독할 때의 경로 아이디 갖고오기: {}", id);

        eventQueueService.setSessionIdChannelMap(sessionId, id);
        eventQueueService.onSubscriptionComplete(id);
        log.info("{}번 구독 완료 처리", id);
    }

    @EventListener
    public void handleWebSocketDisconnectEvent(SessionDisconnectEvent event) {
        log.info("웹소켓 디스커넥트 이벤트 포착 가능");
        log.info("디스커넥팅 시점의 세션 아이디: {}", event.getSessionId());

        /**
         * 구독이 종료되면 이벤트 큐 서비스의 해당 채널 관련 필드들도 초기화시켜줘야함
         */
        eventQueueService.removeAllFields(event.getSessionId());
    }

}
