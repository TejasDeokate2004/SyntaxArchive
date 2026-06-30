package com.logicarchive.dto.response;

import com.logicarchive.enums.Visibility;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        String title,
        String description,
        String codeFileName,
        Long codeFileSize,
        String resourceFileName,
        Long resourceFileSize,
        Visibility visibility,
        Boolean isDeleted,
        LocalDateTime deletedAt,
        Long userId,
        String userName,
        Long folderId,
        String folderName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
