package com.bkb.scanner.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                // Allow CONNECT messages without authentication (authentication happens via query param)
                .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
                // Allow DISCONNECT and UNSUBSCRIBE
                .simpTypeMatchers(SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE).permitAll()
                // Require authentication for SUBSCRIBE
                .simpTypeMatchers(SimpMessageType.SUBSCRIBE).authenticated()
                // Require authentication for sending messages
                .simpTypeMatchers(SimpMessageType.MESSAGE).authenticated()
                // Destination matchers for specific paths
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/topic/**", "/queue/**", "/user/**").authenticated()
                // Deny all other messages
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Disable CSRF for WebSocket to allow cross-origin connections
        return true;
    }
}