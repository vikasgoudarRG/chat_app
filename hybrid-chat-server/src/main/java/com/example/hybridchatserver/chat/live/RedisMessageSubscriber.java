package com.example.hybridchatserver.chat.live;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void handleMessage(String messageJson, String channel) {
        try {
            LiveChatMessage message = objectMapper.readValue(messageJson, LiveChatMessage.class);
            
            String roomName = channel.substring(RedisPubSubConfig.LIVE_CHAT_TOPIC_PATTERN.length() - 1);
            message.setRoom(roomName); 

            String destination = "/topic/live/" + roomName;
            messagingTemplate.convertAndSend(destination, message);

        } catch (IOException e) {
            System.err.println("Could not deserialize message from Redis Pub/Sub: " + messageJson);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String messageBody = new String(message.getBody());
        handleMessage(messageBody, channel);
    }
}
