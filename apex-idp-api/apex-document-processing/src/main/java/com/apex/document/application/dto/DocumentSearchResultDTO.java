package com.apex.document.application.dto;

import java.util.List;

/**
 * DTO for document search results
 */
public class DocumentSearchResultDTO {
    private List<DocumentDTO> documents;
    private long totalCount;
    private int pageNumber;
    private int pageSize;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    
    // Constructors
    public DocumentSearchResultDTO() {}
    
    public DocumentSearchResultDTO(List<DocumentDTO> documents, long totalCount, 
                                 int pageNumber, int pageSize, boolean hasNextPage, 
                                 boolean hasPreviousPage) {
        this.documents = documents;
        this.totalCount = totalCount;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
    }
    
    // Getters and setters
    public List<DocumentDTO> getDocuments() { return documents; }
    public void setDocuments(List<DocumentDTO> documents) { this.documents = documents; }
    
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public boolean isHasNextPage() { return hasNextPage; }
    public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }
    
    public boolean isHasPreviousPage() { return hasPreviousPage; }
    public void setHasPreviousPage(boolean hasPreviousPage) { this.hasPreviousPage = hasPreviousPage; }
}
