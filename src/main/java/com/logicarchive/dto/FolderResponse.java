package com.logicarchive.dto;

import com.logicarchive.entity.Folder;

import java.time.LocalDateTime;

public class FolderResponse {

    private Long id;
    private String name;
    private String description;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FolderResponse() {
    }

    // Build directly from entity - no separate mapper class needed
    public FolderResponse(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.description = folder.getDescription();
        this.userId = folder.getUser().getId();
        this.userName = folder.getUser().getName();
        this.createdAt = folder.getCreatedAt();
        this.updatedAt = folder.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
