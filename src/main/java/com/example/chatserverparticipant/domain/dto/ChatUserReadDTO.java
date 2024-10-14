package com.example.chatserverparticipant.domain.dto;

public record ChatUserReadDTO(String chatId, String email, Boolean read, Boolean leave) {
}