package com.bkb.scanner.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker for broadcasting to connected clients
        config.enableSimpleBroker("/topic", "/queue");

        // Set the application destination prefix for messages bound for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");

        // Set the user destination prefix for user-specific messages
        config.setUserDestinationPrefix("/user");

        log.info("WebSocket message broker configured with prefixes: /topic, /queue, /app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint at /ws
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:5173", "http://localhost:8080")
                .withSockJS(); // Enable SockJS fallback for browsers that don't support WebSocket

        // Also register without SockJS for direct WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:5173", "http://localhost:8080");

        log.info("WebSocket endpoints registered at /ws with SockJS support");
    }
}