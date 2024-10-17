package com.example.chatserverparticipant.domain.controller;

import com.example.chatserverparticipant.domain.service.ChatParticipantService;
import com.example.chatserverparticipant.domain.utility.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ParticipantGraphqlController {

    private final ChatParticipantService chatParticipantService;

    @QueryMapping
    public String getExitTime(@Argument String chatId, @Argument String email) {
        log.info(" *******%%%%%% 퇴장 시간을 갖고 오기 위한 채팅방 번호: {}, 이메일: {} %%%%%%%%%%*******", chatId, email);
        LocalDateTime exitTime = chatParticipantService.getExitTime(chatId, email);
        return DateTimeUtil.toStringTime(exitTime);
    }
}
