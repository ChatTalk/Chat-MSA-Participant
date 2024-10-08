package com.example.chatserverparticipant.global.redis;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
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

    private final DistributedLockFacade distributedLockFacade;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String id = channel.replaceFirst("chat_", "");

        String body = new String(message.getBody());

        log.info("채널 확인: {} // 파싱: {}", channel, id);
        log.info("날 것 그대로의 메세지: " + body);

        try {
            // JSON 파싱
            List<UserReadDTO> dto = new ObjectMapper().readValue(body, new TypeReference<List<UserReadDTO>>() {});
            log.info("파싱: {}", dto.stream().map(e -> e.getEmail() + ": " + e.getIsRead()).toList());

            if (distributedLockFacade.tryLock(id, 5, 60)) {
                messagingTemplate.convertAndSend("/sub/participant/" + id, dto);
                log.info("메시지 전송 완료: {}", id);
                distributedLockFacade.unlock(id);
            } else {
                log.warn("해당 구독 경로에 락이 설정되지 않아 메시지 전송을 중단: {}", id);
            }
        } catch (JsonProcessingException e) {
            log.error("메시지 파싱 오류: {}", e.getMessage());
        }
    }

}
