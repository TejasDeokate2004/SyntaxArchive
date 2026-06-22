package com.logicarchive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FolderCreateRequest(

        @NotBlank(message = "Folder name is required")
        @Size(min = 1, max = 100, message = "Folder name must be between 1 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
