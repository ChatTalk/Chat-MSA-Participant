package com.example.chatserverparticipant.domain.repository;

import com.example.chatserverparticipant.domain.document.ChatParticipant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends MongoRepository<ChatParticipant, String> {
}
