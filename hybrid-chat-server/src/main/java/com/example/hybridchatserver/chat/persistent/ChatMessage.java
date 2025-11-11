package com.example.hybridchatserver.chat.persistent;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "chat_messages")
@Data
public class ChatMessage {
    @Id
    private String id;
    private String from;
    private String to;
    private String content;
    private Instant timestamp;

    public ChatMessage() {
        this.timestamp = Instant.now();
    }
}
