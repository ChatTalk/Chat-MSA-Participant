package com.example.chatserverparticipant.domain.document;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Getter
@Document(collection = "participants")
public class ChatParticipant {

    @Id
    private String id; // MongoDB 기본 키

    @Indexed(unique = true)
    private String chatId;

    private Map<String, Boolean> participant;

    /**
     * @param email
     * 접속 처리
     */
    public void participant(String email) {
        this.participant.put(sanitizeEmail(email), true);
    }

    /**
     * @param email
     * 접속만 끊기
     */
    public void nonParticipant(String email) {
        this.participant.put(sanitizeEmail(email), false);
    }

    /**
     * @param email
     * 채팅방 퇴장
     */
    public void leave(String email) {
        this.participant.remove(sanitizeEmail(email));
    }

    // map 구조의 키에 dot(.)이 붙어있으면 안되는디... 이메일은 닷이 당연히 붙잖아.. 닷컴
    private String sanitizeEmail(String email) {
        return email.replace(".", "-dot-");
    }
}
