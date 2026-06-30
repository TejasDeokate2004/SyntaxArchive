package com.logicarchive.dto;

import com.logicarchive.entity.Note;
import com.logicarchive.enums.Visibility;
import com.logicarchive.service.FileStorageService;

import java.time.LocalDateTime;

public class NoteResponse {

    private Long id;
    private String title;
    private String description;
    private String codeFileUrl;
    private String resourceFileUrl;
    private Visibility visibility;
    private Long userId;
    private String userName;
    private Long folderId;
    private String folderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NoteResponse() {
    }

    // Build directly from entity - file URLs resolved through FileStorageService
    public NoteResponse(Note note, FileStorageService fileStorageService) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.description = note.getDescription();
        this.codeFileUrl = fileStorageService.buildPublicUrl(note.getCodeFileName());
        this.resourceFileUrl = fileStorageService.buildPublicUrl(note.getResourceFileName());
        this.visibility = note.getVisibility();
        this.userId = note.getUser().getId();
        this.userName = note.getUser().getName();
        this.folderId = note.getFolder().getId();
        this.folderName = note.getFolder().getName();
        this.createdAt = note.getCreatedAt();
        this.updatedAt = note.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCodeFileUrl() {
        return codeFileUrl;
    }

    public String getResourceFileUrl() {
        return resourceFileUrl;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getFolderId() {
        return folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
