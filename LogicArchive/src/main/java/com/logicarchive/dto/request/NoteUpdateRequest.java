package com.logicarchive.dto.request;

import com.logicarchive.enums.Visibility;
import jakarta.validation.constraints.Size;

public record NoteUpdateRequest(

        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        String description,

        Visibility visibility
) {
}
