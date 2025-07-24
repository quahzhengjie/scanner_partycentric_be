package com.bkb.scanner.config;

import com.bkb.scanner.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // Try to get userId from query parameter
            String userId = servletRequest.getParameter("userId");
            if (userId != null) {
                attributes.put("userId", userId);
                log.debug("WebSocket handshake for userId: {}", userId);
            }

            // Try to get JWT token from query parameter
            String token = servletRequest.getParameter("token");
            if (token != null) {
                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        String username = jwtTokenProvider.getUsernameFromJWT(token);
                        attributes.put("username", username);
                        log.debug("Valid JWT token for user: {}", username);
                    } else {
                        log.warn("Invalid JWT token provided in WebSocket handshake");
                        return false;
                    }
                } catch (Exception e) {
                    log.error("Error validating JWT token in WebSocket handshake", e);
                    return false;
                }
            }

            // Add session ID
            String sessionId = servletRequest.getSession().getId();
            attributes.put("sessionId", sessionId);
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        }
    }
}