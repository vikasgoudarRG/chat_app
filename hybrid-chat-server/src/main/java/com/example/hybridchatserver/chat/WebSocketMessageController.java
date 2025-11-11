package com.example.hybridchatserver.chat;

import com.example.hybridchatserver.chat.live.LiveChatMessage;
import com.example.hybridchatserver.chat.persistent.ChatMessage;
import com.example.hybridchatserver.chat.persistent.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@AllArgsConstructor
@Slf4j
public class WebSocketMessageController {

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- Module 3: Persistent Chat ---

    @MessageMapping("/chat/persistent")
    public void sendPersistentMessage(@Payload ChatMessage message, Principal principal) {
        // Set 'from' and 'timestamp' from the server-side principal and time
        message.setFrom(principal.getName());
        message.setTimestamp(Instant.now());
        
        // Send to RabbitMQ for processing and persistence
        rabbitTemplate.convertAndSend(RabbitConfig.PERSISTENT_CHAT_QUEUE, message);
        log.info("Received persistent message for RabbitMQ: {} -> {}", message.getFrom(), message.getTo());
    }

    // --- Module 4: Live Chat ---

    @SubscribeMapping("/live/join/{roomName}")
    public void joinLiveRoom(Principal principal, @DestinationVariable String roomName) {
        // This method handles the *subscription* event itself.
        // We can broadcast a "join" message to the room.
        LiveChatMessage joinMessage = new LiveChatMessage();
        joinMessage.setFrom("SYSTEM");
        joinMessage.setRoom(roomName);
        joinMessage.setContent(principal.getName() + " has joined the room.");

        // We can send this via Redis Pub/Sub just like a normal message
        // Or send it directly, but Pub/Sub is cleaner.
        try {
            String topic = "live-chat:" + roomName;
            redisTemplate.convertAndSend(topic, objectMapper.writeValueAsString(joinMessage));
        } catch (Exception e) {
            log.error("Could not serialize join message", e);
        }
    }

    @MessageMapping("/chat/live/say")
    public void sendLiveMessage(@Payload LiveChatMessage message, Principal principal) {
        // Set 'from' from the server-side principal
        message.setFrom(principal.getName());
        
        // Publish to Redis Pub/Sub topic
        try {
            String topic = "live-chat:" + message.getRoom();
            String messageJson = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(topic, messageJson);
        } catch (Exception e) {
            log.error("Could not serialize and publish live message", e);
        }
    }
}
