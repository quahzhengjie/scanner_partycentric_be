package com.bkb.scanner.service;

import com.bkb.scanner.dto.NotificationDTO;
import com.bkb.scanner.entity.Case;
import com.bkb.scanner.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // In-memory storage for notifications (in production, use database)
    private final Map<String, List<NotificationDTO>> userNotifications = new ConcurrentHashMap<>();

    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(String userId, NotificationDTO notification) {
        // Store notification
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);

        // Send via WebSocket if user is connected
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                notification
        );

        log.info("Sent notification to user {}: {}", userId, notification.getTitle());
    }

    /**
     * Send notification to multiple users
     */
    public void sendNotificationToUsers(List<String> userIds, NotificationDTO notification) {
        userIds.forEach(userId -> sendNotificationToUser(userId, notification));
    }

    /**
     * Send notification to all users with a specific role
     */
    public void sendNotificationToRole(User.UserRole role, NotificationDTO notification) {
        // In a real implementation, you would fetch users by role from database
        // For now, we'll broadcast to a role-specific topic
        messagingTemplate.convertAndSend(
                "/topic/role/" + role.name(),
                notification
        );

        log.info("Sent notification to role {}: {}", role, notification.getTitle());
    }

    /**
     * Broadcast notification to all connected users
     */
    public void broadcastNotification(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("Broadcasted notification: {}", notification.getTitle());
    }

    /**
     * Get unread notifications for a user
     */
    public List<NotificationDTO> getUnreadNotifications(String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>())
                .stream()
                .filter(n -> !n.isRead())
                .toList();
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(String userId, String notificationId) {
        List<NotificationDTO> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.stream()
                    .filter(n -> n.getId().equals(notificationId))
                    .findFirst()
                    .ifPresent(n -> {
                        n.setRead(true);
                        // Notify user of read status change
                        messagingTemplate.convertAndSendToUser(
                                userId,
                                "/queue/notification-read",
                                notificationId
                        );
                    });
        }
    }

    /**
     * Clear all notifications for a user
     */
    public void clearNotifications(String userId) {
        userNotifications.remove(userId);
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications-cleared",
                "All notifications cleared"
        );
    }

    // Notification creation helpers for common scenarios

    public void notifyCaseStatusChange(Case caseEntity, String previousStatus, String actor) {
        NotificationDTO notification = NotificationDTO.caseStatusChanged(
                caseEntity.getCaseId(),
                caseEntity.getEntityData().getEntityName(),
                previousStatus,
                caseEntity.getStatus().name(),
                actor
        );

        // Send to assigned user
        if (caseEntity.getAssignedTo() != null) {
            sendNotificationToUser(caseEntity.getAssignedTo(), notification);
        }

        // Send to appropriate role based on new status
        switch (caseEntity.getStatus()) {
            case PENDING_CHECKER_REVIEW:
                sendNotificationToRole(User.UserRole.CHECKER, notification);
                break;
            case PENDING_COMPLIANCE_REVIEW:
                sendNotificationToRole(User.UserRole.COMPLIANCE, notification);
                break;
            case PENDING_GM_APPROVAL:
                sendNotificationToRole(User.UserRole.GM, notification);
                break;
            default:
                break;
        }
    }

    public void notifyDocumentSubmission(String caseId, String documentType,
                                         String submittedBy, String assignedChecker) {
        NotificationDTO notification = NotificationDTO.documentSubmitted(
                caseId, documentType, submittedBy
        );

        // Send to assigned checker
        if (assignedChecker != null) {
            sendNotificationToUser(assignedChecker, notification);
        }

        // Also send to all checkers
        sendNotificationToRole(User.UserRole.CHECKER, notification);
    }

    public void notifyDocumentRejection(String caseId, String documentType,
                                        String rejectedBy, String reason, String originalSubmitter) {
        NotificationDTO notification = NotificationDTO.documentRejected(
                caseId, documentType, rejectedBy, reason
        );

        // Send to original submitter
        sendNotificationToUser(originalSubmitter, notification);
    }

    public void notifyCaseAssignment(String caseId, String caseName,
                                     String assignedTo, String assignedBy) {
        NotificationDTO notification = NotificationDTO.caseAssigned(
                caseId, caseName, assignedTo, assignedBy
        );

        sendNotificationToUser(assignedTo, notification);
    }

    public void notifyApprovalRequired(Case caseEntity) {
        String approverRole = determineApproverRole(caseEntity.getStatus());

        NotificationDTO notification = NotificationDTO.approvalRequired(
                caseEntity.getCaseId(),
                caseEntity.getEntityData().getEntityName(),
                approverRole
        );

        // Send to appropriate role
        switch (caseEntity.getStatus()) {
            case PENDING_CHECKER_REVIEW:
                sendNotificationToRole(User.UserRole.CHECKER, notification);
                break;
            case PENDING_COMPLIANCE_REVIEW:
                sendNotificationToRole(User.UserRole.COMPLIANCE, notification);
                break;
            case PENDING_GM_APPROVAL:
                sendNotificationToRole(User.UserRole.GM, notification);
                break;
            default:
                break;
        }
    }

    private String determineApproverRole(Case.CaseStatus status) {
        return switch (status) {
            case PENDING_CHECKER_REVIEW -> "Checker";
            case PENDING_COMPLIANCE_REVIEW -> "Compliance";
            case PENDING_GM_APPROVAL -> "General Manager";
            default -> "Unknown";
        };
    }
}