package com.example.chatserverparticipant.global.redis;

import com.example.chatserverparticipant.domain.dto.ChatUserReadDTO;
import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.service.ChatParticipantService;
import com.example.chatserverparticipant.domain.service.EventQueueService;
import com.example.chatserverparticipant.global.facade.DistributedLockFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisMessageListenerService implements MessageListener {

    private final EventQueueService eventQueueService;
    private final ChatParticipantService chatParticipantService;

    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String id = channel.replaceFirst("chat_", "");

        String body = new String(message.getBody());

        log.info("채널 확인: {} // 파싱: {}", channel, id);

        try {
            // JSON 파싱
            ChatUserReadDTO dto = new ObjectMapper().readValue(body, ChatUserReadDTO.class);
            log.info("레코드 파싱: {}", dto.toString());

            List<UserReadDTO> data = chatParticipantService.getParticipantsList(dto);
            log.info("파싱: {}", data.stream().map(e -> e.getEmail() + ": " + e.getIsRead()).toList());

            if (eventQueueService.isSubscriptionComplete(id)) {
                // 구독 완료 -> 바로 전송
                log.info("{}번 구독 확인, 송신", id);
                messagingTemplate.convertAndSend("/sub/participant/" + id, data);
            } else {
                // 구독 미완료 -> 대기열에 저장
                log.info("{}번 구독 미완료, 데이터 대기열에 저장: {}", id, data);
                eventQueueService.enqueueMessage(id, data);
            }

        } catch (JsonProcessingException e) {
            log.error("메시지 파싱 오류: {}", e.getMessage());
        }
    }

}
