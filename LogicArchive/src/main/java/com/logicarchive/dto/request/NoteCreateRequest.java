package com.logicarchive.dto.request;

import com.logicarchive.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteCreateRequest(

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        String description,

        @NotNull(message = "Folder ID is required")
        Long folderId,

        Visibility visibility
) {
}
