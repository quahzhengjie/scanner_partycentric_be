package com.bkb.scanner.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    // Track connected users
    private final Map<String, String> connectedUsers = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        if (userId != null) {
            connectedUsers.put(sessionId, userId);
            log.info("User connected: {} with session: {}", userId, sessionId);

            // Notify other users about the new connection
            broadcastUserStatus(userId, true);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = connectedUsers.remove(sessionId);

        if (userId != null) {
            log.info("User disconnected: {} from session: {}", userId, sessionId);

            // Notify other users about the disconnection
            broadcastUserStatus(userId, false);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        String userId = connectedUsers.get(sessionId);

        log.debug("User {} subscribed to: {}", userId, destination);
    }

    private void broadcastUserStatus(String userId, boolean isOnline) {
        UserStatusMessage statusMessage = new UserStatusMessage(userId, isOnline);
        messagingTemplate.convertAndSend("/topic/user-status", statusMessage);
    }

    // Inner class for user status messages
    public static class UserStatusMessage {
        private final String userId;
        private final boolean online;
        private final long timestamp;

        public UserStatusMessage(String userId, boolean online) {
            this.userId = userId;
            this.online = online;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getUserId() { return userId; }
        public boolean isOnline() { return online; }
        public long getTimestamp() { return timestamp; }
    }
}