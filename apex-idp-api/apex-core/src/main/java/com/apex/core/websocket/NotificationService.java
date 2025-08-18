package com.apex.core.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Service for sending real-time notifications via WebSocket
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send document processing status update to user
     */
    public void sendDocumentProcessingUpdate(UUID userId, String documentId, String status, Map<String, Object> details) {
        String destination = "/user/" + userId + "/topic/documents";
        
        Map<String, Object> message = Map.of(
            "type", "DOCUMENT_PROCESSING_UPDATE",
            "documentId", documentId,
            "status", status,
            "details", details,
            "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend(destination, message);
        log.debug("Sent document processing update to user {}: {}", userId, status);
    }

    /**
     * Send financial operation notification
     */
    public void sendFinancialNotification(UUID userId, String operationType, Map<String, Object> data) {
        String destination = "/user/" + userId + "/topic/financial";
        
        Map<String, Object> message = Map.of(
            "type", "FINANCIAL_NOTIFICATION",
            "operationType", operationType,
            "data", data,
            "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend(destination, message);
        log.debug("Sent financial notification to user {}: {}", userId, operationType);
    }

    /**
     * Send contract management update
     */
    public void sendContractUpdate(UUID userId, String contractId, String status, Map<String, Object> details) {
        String destination = "/user/" + userId + "/topic/contracts";
        
        Map<String, Object> message = Map.of(
            "type", "CONTRACT_UPDATE",
            "contractId", contractId,
            "status", status,
            "details", details,
            "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend(destination, message);
        log.debug("Sent contract update to user {}: {}", userId, status);
    }

    /**
     * Send vendor status update
     */
    public void sendVendorUpdate(UUID organizationId, String vendorId, String status, Map<String, Object> details) {
        String destination = "/topic/vendors/" + organizationId;
        
        Map<String, Object> message = Map.of(
            "type", "VENDOR_UPDATE",
            "vendorId", vendorId,
            "status", status,
            "details", details,
            "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend(destination, message);
        log.debug("Sent vendor update to organization {}: {}", organizationId, status);
    }

    /**
     * Send general notification to user
     */
    public void sendNotification(UUID userId, String title, String message, String type) {
        String destination = "/user/" + userId + "/topic/notifications";
        
        Map<String, Object> notification = Map.of(
            "type", "GENERAL_NOTIFICATION",
            "title", title,
            "message", message,
            "notificationType", type,
            "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend(destination, notification);
        log.debug("Sent notification to user {}: {}", userId, title);
    }

    /**
     * Broadcast system-wide notification
     */
    public void broadcastSystemNotification(String title, String message, String type) {
        Map<String, Object> notification = Map.of(
            "type", "SYSTEM_NOTIFICATION",
            "title", title,
            "message", message,
            "notificationType", type,
            "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("Broadcasted system notification: {}", title);
    }
}
