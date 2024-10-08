package com.example.chatserverparticipant.domain.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientSessionManager {

    /**
     * 서버가 다운됐다가 복구됐을 때는 어떻게 하려나? 세션 연결 여부를 저장하는 걸 텐데
     * 개인적인 생각: 이벤트식으로 업뎃된 redisTemplate 바로 조회해서 넣어주기?
     * -> 이걸 아예 서버 초기화를 기점으로 해서 초기값을 redisTemplate 해시 엔트리 값으로 삼기?
     */
    private final Map<String, Boolean> connectedClients = new ConcurrentHashMap<>();

    public void addClient(String chatId) {
        connectedClients.put(chatId, true);
    }

    public void removeClient(String chatId) {
        connectedClients.remove(chatId);
    }

    public boolean isClientConnected(String chatId) {
        return connectedClients.containsKey(chatId);
    }
}
