package com.apex.document.application.command;

import com.apex.core.cqrs.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * Command to upload a new document for processing
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UploadDocumentCommand extends Command {
    private final String fileName;
    private final String contentType;
    private final long fileSize;
    private final byte[] content;
    private final String documentType;
    private final UUID vendorId;
    private final UUID userId;
    
    public UploadDocumentCommand(String fileName, String contentType, long fileSize, 
                               byte[] content, String documentType, UUID vendorId, UUID userId) {
        super(UUID.randomUUID(), userId);
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.content = content;
        this.documentType = documentType;
        this.vendorId = vendorId;
        this.userId = userId;
    }
}
