package com.example.chatserverparticipant.domain.service;

import com.example.chatserverparticipant.domain.document.ChatParticipant;
import com.example.chatserverparticipant.domain.dto.ChatUserReadDTO;
import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import com.example.chatserverparticipant.domain.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "MONGODB_PARTICIPANT_SERVICE")
@Service
@RequiredArgsConstructor
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;

    // 채팅방 읽음
    public void read(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        chatParticipant.getParticipant().put(
                chatUserReadDTO.email(), true
        );

        chatParticipantRepository.save(chatParticipant);
    }

    // 채팅방 안 읽음
    public void exit(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        chatParticipant.getParticipant().put(
                chatUserReadDTO.email(), false
        );

        chatParticipantRepository.save(chatParticipant);
    }

    // 채팅방 구독 종료
    public void leave(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        chatParticipant.getParticipant().remove(chatUserReadDTO.email());

        chatParticipantRepository.save(chatParticipant);
    }

    // 채팅방 리스트 반환하기
    public List<UserReadDTO> getParticipantsList(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        Map<String, Boolean> participants = chatParticipant.getParticipant();

        return participants.entrySet()
                .stream()
                .map(e -> new UserReadDTO(e.getKey(), e.getValue())).toList();
    }

    private ChatParticipant findChatParticipant(String chatId) {
        return chatParticipantRepository.findByChatId(chatId).orElseThrow(
                () -> new IllegalArgumentException("Chat participant not found")
        );
    }

}
