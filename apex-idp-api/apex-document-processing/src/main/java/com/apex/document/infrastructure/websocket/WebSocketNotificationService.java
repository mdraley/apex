package com.apex.document.infrastructure.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for WebSocket notifications
 */
@Service
public class WebSocketNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void notifyDocumentProcessed(String userId, String documentId) {
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/document-processed", 
            documentId
        );
    }
    
    public void notifyDocumentUploaded(java.util.UUID documentId) {
        // TODO: Implement notification for document upload
        System.out.println("Document uploaded: " + documentId);
    }
    
    public void notifyValidationComplete(String userId, String documentId) {
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/validation-complete", 
            documentId
        );
    }
    
    public void notifyError(String userId, String message) {
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/error", 
            message
        );
    }
}
