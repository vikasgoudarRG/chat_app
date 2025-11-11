package com.example.hybridchatserver.chat.persistent;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
public class MessageHistoryController {

    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/history/{username}")
    public List<ChatMessage> getChatHistory(@PathVariable String username, Principal principal) {
        String currentUser = principal.getName();
        return chatMessageRepository.findChatHistory(currentUser, username);
    }
}
