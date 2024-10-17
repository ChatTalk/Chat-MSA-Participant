package com.example.chatserverparticipant.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ParticipantInfoDTO {
    private Boolean isAccessed;
    private LocalDateTime exitTime;

    // 채팅방 접속 초기화
    public ParticipantInfoDTO() {
        this.isAccessed = true;
        this.exitTime = LocalDateTime.now();
    }
}
