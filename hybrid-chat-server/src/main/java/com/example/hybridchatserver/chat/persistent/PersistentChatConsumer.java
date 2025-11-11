package com.example.hybridchatserver.chat.persistent;

import com.example.hybridchatserver.presence.PresenceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PersistentChatConsumer {

    private final ChatMessageRepository chatMessageRepository;
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    public PersistentChatConsumer(ChatMessageRepository chatMessageRepository,
                                  PresenceService presenceService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitConfig.PERSISTENT_CHAT_QUEUE, concurrency = "1")
    public void handlePersistentMessage(ChatMessage message) {
        chatMessageRepository.save(message);

        if (presenceService.isUserOnline(message.getTo())) {
            messagingTemplate.convertAndSendToUser(
                message.getTo(),
                "/queue/messages",
                message
            );
            
        }
    }
}
