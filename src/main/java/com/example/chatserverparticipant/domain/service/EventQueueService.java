package com.example.chatserverparticipant.domain.service;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j(topic = "QUEUE_SERVICE")
@Component
@RequiredArgsConstructor
public class EventQueueService {

    private final Map<String, String> sessionIdChannelMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> subscriptionFlags = new ConcurrentHashMap<>();
    private final Map<String, Queue<List<UserReadDTO>>> messageQueue = new ConcurrentHashMap<>();
    private final SimpMessageSendingOperations messagingTemplate;

    // 세션 아이디 - 채널 저장
    public void setSessionIdChannelMap(String sessionId, String chatId) {
        sessionIdChannelMap.put(sessionId, chatId);
    }

    // 세션 아이디 - 채널 및 채널의 플래그, 큐 삭제
    public void removeAllFields(String sessionId) {
        String chatId = sessionIdChannelMap.get(sessionId);

        if (chatId == null) {
            throw new IllegalArgumentException("채팅 아이디가 세션 맵에 저장 안 됨");
        }

        subscriptionFlags.remove(chatId);
        messageQueue.remove(chatId);
        sessionIdChannelMap.remove(sessionId);
    }

    // 구독 완료 처리
    public void onSubscriptionComplete(String clientId) {
        subscriptionFlags.put(clientId, true);
        flushQueueForClient(clientId);
    }

    // 구독 완료 여부 확인
    public boolean isSubscriptionComplete(String clientId) {
        return subscriptionFlags.getOrDefault(clientId, false);
    }

    // 대기열에 메시지 저장
    public void enqueueMessage(String clientId, List<UserReadDTO> message) {
        messageQueue.computeIfAbsent(clientId, k -> new ConcurrentLinkedQueue<>()).add(message);
    }

    // 대기열 처리
    private void flushQueueForClient(String clientId) {
        log.info("{}번 큐 플러시 과정 돌입", clientId);
        Queue<List<UserReadDTO>> queue = messageQueue.get(clientId);
        if (queue != null) {
            while (!queue.isEmpty()) {
                log.info("{}번 큐 데이터 산입 확인: {}", clientId, queue.size());
                List<UserReadDTO> message = queue.poll();
                // 대기열에 쌓인 메시지를 클라이언트로 전송하는 로직
                sendMessageToClient(clientId, message);
                log.info("*** 데이터 송신 완료 ***");
            }
        }
    }

    private void sendMessageToClient(String clientId, List<UserReadDTO> message) {
        // WebSocket 클라이언트로 메시지를 전송하는 로직
        log.info("대기열 이후 최종 메세지 송신: {}번", clientId);
        messagingTemplate.convertAndSend("/sub/participant/" + clientId, message);
    }

}
