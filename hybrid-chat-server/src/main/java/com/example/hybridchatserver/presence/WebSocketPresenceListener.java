package com.example.hybridchatserver.presence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketPresenceListener {

    private final PresenceService presenceService;

    public WebSocketPresenceListener(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        try {
            String username = Objects.requireNonNull(headers.getUser()).getName();
            presenceService.setUserOnline(username);
        } catch (NullPointerException e) {
            System.err.println("User connected without a principal. This might be a health check.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        try {
            String username = Objects.requireNonNull(headers.getUser()).getName();
            presenceService.setUserOffline(username);
        } catch (NullPointerException e) {
             System.err.println("User disconnected without a principal.");
        }
    }
}
