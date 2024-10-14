package com.example.chatserverparticipant.domain.document;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Getter
@Document(collection = "participants")
public class ChatParticipant {

    @Id
    private String id; // MongoDB 기본 키

    private String chatId;

    private Map<String, Boolean> participant;

    /**
     * @param email
     * 접속 처리
     */
    public void participant(String email) {
        this.participant.put(email, true);
    }

    /**
     * @param email
     * 접속만 끊기
     */
    public void nonParticipant(String email) {
        this.participant.put(email, false);
    }

    /**
     * @param email
     * 채팅방 퇴장
     */
    public void exit(String email) {
        this.participant.remove(email);
    }
}
