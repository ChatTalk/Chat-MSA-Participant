package com.example.chatserverparticipant.domain.repository;

import com.example.chatserverparticipant.domain.document.ChatParticipant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends MongoRepository<ChatParticipant, String> {

    Optional<ChatParticipant> findByChatId(String chatId);
}
