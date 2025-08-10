package org.example.chatservice.dao;

import org.example.chatservice.entity.ConversationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationDao extends MongoRepository<ConversationEntity, String> {
    Optional<ConversationEntity> findByParticipantsHash(String hash);

    @Query("{'participants.userId' : ?0}")
    List<ConversationEntity> findAllByParticipantIdsContains(String userId);
}
