package com.bkb.scanner.controller;

import com.bkb.scanner.dto.NotificationDTO;
import com.bkb.scanner.entity.ActivityLog;
import com.bkb.scanner.entity.Case;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle ping messages to keep connection alive
     */
    @MessageMapping("/ping")
    @SendToUser("/queue/pong")
    public String handlePing(Principal principal) {
        log.debug("Ping received from user: {}", principal.getName());
        return "pong";
    }

    /**
     * Broadcast case updates to all connected users
     */
    public void broadcastCaseUpdate(Case updatedCase, String action) {
        CaseUpdateMessage message = new CaseUpdateMessage(
                updatedCase.getCaseId(),
                updatedCase.getStatus().name(),
                action,
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/topic/case-updates", message);
        log.info("Broadcasted case update: {} - {}", updatedCase.getCaseId(), action);
    }

    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(String userId, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                notification
        );
        log.info("Sent notification to user: {}", userId);
    }

    /**
     * Broadcast activity log to interested users
     */
    public void broadcastActivity(ActivityLog activity) {
        ActivityMessage message = new ActivityMessage(
                activity.getActivityId(),
                activity.getCaseEntity().getCaseId(),
                activity.getActor(),
                activity.getAction(),
                activity.getTimestamp()
        );

        messagingTemplate.convertAndSend("/topic/activities", message);
    }

    /**
     * Handle test messages
     */
    @MessageMapping("/test")
    @SendTo("/topic/test-response")
    public TestResponse handleTestMessage(@Payload TestMessage message, Principal principal) {
        log.info("Test message received from {}: {}", principal.getName(), message.getContent());
        return new TestResponse(
                "Echo: " + message.getContent(),
                principal.getName(),
                System.currentTimeMillis()
        );
    }

    // Message DTOs
    public static class CaseUpdateMessage {
        private final String caseId;
        private final String status;
        private final String action;
        private final LocalDateTime timestamp;

        public CaseUpdateMessage(String caseId, String status, String action, LocalDateTime timestamp) {
            this.caseId = caseId;
            this.status = status;
            this.action = action;
            this.timestamp = timestamp;
        }

        // Getters
        public String getCaseId() { return caseId; }
        public String getStatus() { return status; }
        public String getAction() { return action; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    public static class ActivityMessage {
        private final String activityId;
        private final String caseId;
        private final String actor;
        private final String action;
        private final LocalDateTime timestamp;

        public ActivityMessage(String activityId, String caseId, String actor, String action, LocalDateTime timestamp) {
            this.activityId = activityId;
            this.caseId = caseId;
            this.actor = actor;
            this.action = action;
            this.timestamp = timestamp;
        }

        // Getters
        public String getActivityId() { return activityId; }
        public String getCaseId() { return caseId; }
        public String getActor() { return actor; }
        public String getAction() { return action; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    public static class TestMessage {
        private String content;

        // Getter and Setter
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class TestResponse {
        private final String message;
        private final String sender;
        private final long timestamp;

        public TestResponse(String message, String sender, long timestamp) {
            this.message = message;
            this.sender = sender;
            this.timestamp = timestamp;
        }

        // Getters
        public String getMessage() { return message; }
        public String getSender() { return sender; }
        public long getTimestamp() { return timestamp; }
    }
}