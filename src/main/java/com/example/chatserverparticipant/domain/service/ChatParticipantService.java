package com.example.chatserverparticipant.domain.service;

import com.example.chatserverparticipant.domain.document.ChatParticipant;
import com.example.chatserverparticipant.domain.dto.ChatUserReadDTO;
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
            leave(chatUserReadDTO);
        } else {
            if (chatUserReadDTO.read()) {
                read(chatUserReadDTO);
            } else {
                exit(chatUserReadDTO);
            }
        }
    }

    // 채팅방 리스트 반환하기
    public List<UserReadDTO> getParticipantsList(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
        Map<String, Boolean> participants = chatParticipant.getParticipant();

        return participants.entrySet()
                .stream()
                .map(e -> new UserReadDTO(e.getKey(), e.getValue())).toList();
    }

    // 채팅방 읽음
    private void read(ChatUserReadDTO chatUserReadDTO) {
        ChatParticipant chatParticipant = findChatParticipant(chatUserReadDTO.chatId());
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
        return chatParticipantRepository.findByChatId(chatId).orElseThrow(
                () -> new IllegalArgumentException("Chat participant not found")
        );
    }

}
