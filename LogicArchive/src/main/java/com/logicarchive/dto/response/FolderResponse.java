package com.logicarchive.dto.response;

import java.time.LocalDateTime;

public record FolderResponse(
        Long id,
        String name,
        String description,
        Long userId,
        String userName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
