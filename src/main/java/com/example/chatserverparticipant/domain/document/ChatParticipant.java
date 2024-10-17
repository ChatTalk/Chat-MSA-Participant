package com.example.chatserverparticipant.domain.document;

import com.example.chatserverparticipant.domain.dto.ParticipantInfoDTO;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Getter
@Document(collection = "participants")
public class ChatParticipant {

    @Id
    private String id; // MongoDB 기본 키

    @Indexed(unique = true)
    private String chatId;

    private Map<String, ParticipantInfoDTO> participant;

    /**
     * @param email
     * 접속 처리
     */
    public void participant(String email) {
        ParticipantInfoDTO info = this.participant.get(email);

        if (info == null) {
            info = new ParticipantInfoDTO();
        } else {
            info.setIsAccessed(true);
        }

        this.participant.put(sanitizeEmail(email), info);
    }

    /**
     * @param email
     * 접속만 끊기
     */
    public void nonParticipant(String email) {
        ParticipantInfoDTO info = this.participant.get(sanitizeEmail(email));
        info.setIsAccessed(false);
        info.setExitTime(LocalDateTime.now());

        this.participant.put(sanitizeEmail(email), info);
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
