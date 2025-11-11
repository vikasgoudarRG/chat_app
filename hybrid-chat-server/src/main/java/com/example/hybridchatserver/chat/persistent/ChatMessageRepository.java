package com.example.hybridchatserver.chat.persistent;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    // Find messages between two users, ordered by time
    @Query(value = "{'$or': [{'from': ?0, 'to': ?1}, {'from': ?1, 'to': ?0}]}",
           sort = "{'timestamp': 1}")
    List<ChatMessage> findChatHistory(String user1, String user2);
}
