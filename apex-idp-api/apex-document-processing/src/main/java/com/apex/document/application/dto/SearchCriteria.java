package com.apex.document.application.dto;

import com.apex.document.domain.DocumentType;
import lombok.Builder;
import lombok.Data;
import java.util.Map;

/**
 * DTO for search criteria
 */
@Data
@Builder
public class SearchCriteria {
    private String query;
    private DocumentType documentType;
    private String userId;
    private String dateFrom;
    private String dateTo;
    private String stage;
    private Map<String, Object> filters;
    private String sortBy;
    private String sortDirection;
    private int page;
    private int size;
    
    // Constructors
    public SearchCriteria() {}
    
    public SearchCriteria(String query, DocumentType documentType, String userId,
                         String dateFrom, String dateTo, String stage,
                         Map<String, Object> filters, String sortBy, String sortDirection,
                         int page, int size) {
        this.query = query;
        this.documentType = documentType;
        this.userId = userId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.stage = stage;
        this.filters = filters;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.page = page;
        this.size = size;
    }
    
    // Getters and setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
    
    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }
    
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    
    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
