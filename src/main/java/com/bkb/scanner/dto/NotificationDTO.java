package com.bkb.scanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String id;
    private String type;
    private String title;
    private String message;
    private String userId;
    private String relatedEntityId;
    private String relatedEntityType;
    private NotificationSeverity severity;
    private boolean read;
    private LocalDateTime timestamp;
    private String actionUrl;
    private NotificationMetadata metadata;

    public enum NotificationSeverity {
        INFO,
        WARNING,
        ERROR,
        SUCCESS
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationMetadata {
        private String caseId;
        private String accountId;
        private String documentId;
        private String actorName;
        private String previousStatus;
        private String newStatus;
        private String additionalInfo;
    }

    // Factory methods for common notification types

    public static NotificationDTO caseStatusChanged(String caseId, String caseName,
                                                    String previousStatus, String newStatus, String actorName) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("CASE_STATUS_CHANGED")
                .title("Case Status Updated")
                .message(String.format("Case %s status changed from %s to %s by %s",
                        caseName, previousStatus, newStatus, actorName))
                .relatedEntityId(caseId)
                .relatedEntityType("CASE")
                .severity(NotificationSeverity.INFO)
                .read(false)
                .timestamp(LocalDateTime.now())
                .actionUrl("/cases/" + caseId)
                .metadata(NotificationMetadata.builder()
                        .caseId(caseId)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .actorName(actorName)
                        .build())
                .build();
    }

    public static NotificationDTO documentSubmitted(String caseId, String documentType,
                                                    String submittedBy) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("DOCUMENT_SUBMITTED")
                .title("New Document Submitted")
                .message(String.format("%s submitted %s for case %s",
                        submittedBy, documentType, caseId))
                .relatedEntityId(caseId)
                .relatedEntityType("CASE")
                .severity(NotificationSeverity.INFO)
                .read(false)
                .timestamp(LocalDateTime.now())
                .actionUrl("/cases/" + caseId + "/documents")
                .metadata(NotificationMetadata.builder()
                        .caseId(caseId)
                        .actorName(submittedBy)
                        .additionalInfo(documentType)
                        .build())
                .build();
    }

    public static NotificationDTO documentRejected(String caseId, String documentType,
                                                   String rejectedBy, String reason) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("DOCUMENT_REJECTED")
                .title("Document Rejected")
                .message(String.format("%s rejected %s: %s",
                        rejectedBy, documentType, reason))
                .relatedEntityId(caseId)
                .relatedEntityType("CASE")
                .severity(NotificationSeverity.WARNING)
                .read(false)
                .timestamp(LocalDateTime.now())
                .actionUrl("/cases/" + caseId + "/documents")
                .metadata(NotificationMetadata.builder()
                        .caseId(caseId)
                        .actorName(rejectedBy)
                        .additionalInfo(reason)
                        .build())
                .build();
    }

    public static NotificationDTO caseAssigned(String caseId, String caseName,
                                               String assignedTo, String assignedBy) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("CASE_ASSIGNED")
                .title("Case Assigned to You")
                .message(String.format("Case %s has been assigned to you by %s",
                        caseName, assignedBy))
                .userId(assignedTo)
                .relatedEntityId(caseId)
                .relatedEntityType("CASE")
                .severity(NotificationSeverity.INFO)
                .read(false)
                .timestamp(LocalDateTime.now())
                .actionUrl("/cases/" + caseId)
                .metadata(NotificationMetadata.builder()
                        .caseId(caseId)
                        .actorName(assignedBy)
                        .build())
                .build();
    }

    public static NotificationDTO approvalRequired(String caseId, String caseName,
                                                   String approverRole) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("APPROVAL_REQUIRED")
                .title("Approval Required")
                .message(String.format("Case %s requires %s approval",
                        caseName, approverRole))
                .relatedEntityId(caseId)
                .relatedEntityType("CASE")
                .severity(NotificationSeverity.WARNING)
                .read(false)
                .timestamp(LocalDateTime.now())
                .actionUrl("/cases/" + caseId)
                .metadata(NotificationMetadata.builder()
                        .caseId(caseId)
                        .additionalInfo(approverRole)
                        .build())
                .build();
    }

    public static NotificationDTO accountActivated(String accountId, String accountNumber,
                                                   String caseId, String activatedBy) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("ACCOUNT_ACTIVATED")
                .title("Account Activated")
                .message(String.format("Account %s has been activated by %s",
                        accountNumber, activatedBy))
                .relatedEntityId(accountId)
                .relatedEntityType("ACCOUNT")
                .severity(NotificationSeverity.SUCCESS)
                .read(false)
                .timestamp(LocalDateTime.now())
                .actionUrl("/cases/" + caseId + "/accounts")
                .metadata(NotificationMetadata.builder()
                        .caseId(caseId)
                        .accountId(accountId)
                        .actorName(activatedBy)
                        .build())
                .build();
    }

    public static NotificationDTO systemAlert(String title, String message,
                                              NotificationSeverity severity) {
        return NotificationDTO.builder()
                .id(generateId())
                .type("SYSTEM_ALERT")
                .title(title)
                .message(message)
                .severity(severity)
                .read(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private static String generateId() {
        return "NOTIF-" + System.currentTimeMillis() + "-" +
                (int)(Math.random() * 10000);
    }
}