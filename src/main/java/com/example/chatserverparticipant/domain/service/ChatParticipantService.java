package com.example.chatserverparticipant.domain.service;

import com.example.chatserverparticipant.domain.document.ChatParticipant;
import com.example.chatserverparticipant.domain.dto.ChatUserReadDTO;
import com.example.chatserverparticipant.domain.dto.ParticipantInfoDTO;
import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j(topic = "MONGODB_PARTICIPANT_SERVICE")
@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    public void service(ChatUserReadDTO chatUserReadDTO) {
        // 채팅방을 떠났는가?(구독 종료)
        if (chatUserReadDTO.leave()) {
            log.info("채팅방 구독 종료");
            leave(chatUserReadDTO);
        } else {
            if (chatUserReadDTO.read()) {
                log.info("채팅방에 접속");
                read(chatUserReadDTO);
            } else {
                log.info("채팅방 접속 종료");
                exit(chatUserReadDTO);
            }
        }
    }

    // 채팅방 리스트 반환하기
    public List<UserReadDTO> getParticipantsList(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        Map<String, ParticipantInfoDTO> participants = chatParticipant.getParticipant();

        return participants.entrySet()
                .stream()
                .map(e -> new UserReadDTO(
                        e.getKey().replace("-dot-", "."),
                        e.getValue().getIsAccessed()))
                .toList();
    }

    // 채팅방 읽음
    private void read(ChatUserReadDTO chatUserReadDTO) {
        log.info("디티오: {}", chatUserReadDTO);
        log.info("채팅방 아이디: {}", chatUserReadDTO.chatId());

        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        log.info("서비스 로직에서 채팅방 도큐먼트 찾기: {}", chatParticipant);
        chatParticipant.participant(chatUserReadDTO.email());

        chatParticipantRepository.save(chatParticipant);
    }

    // 채팅방 안 읽음
    private void exit(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        chatParticipant.nonParticipant(chatUserReadDTO.email());

        chatParticipantRepository.save(chatParticipant);
    }

    // 채팅방 구독 종료
    private void leave(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        chatParticipant.leave(chatUserReadDTO.email());

        chatParticipantRepository.save(chatParticipant);
    }

    private ChatParticipant findChatParticipant(String chatId) {
        log.info("한번 다 찾아보자: {}", chatParticipantRepository.findAll());

        return chatParticipantRepository.findByChatId(chatId).orElseThrow(
                () -> new IllegalArgumentException("Chat participant not found")
        );
    }

}
