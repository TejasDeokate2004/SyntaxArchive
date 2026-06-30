package com.logicarchive.mapper;

import com.logicarchive.dto.response.NoteResponse;
import com.logicarchive.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getDescription(),
                note.getCodeFileName(),
                note.getCodeFileSize(),
                note.getResourceFileName(),
                note.getResourceFileSize(),
                note.getVisibility(),
                note.getIsDeleted(),
                note.getDeletedAt(),
                note.getUser().getId(),
                note.getUser().getName(),
                note.getFolder().getId(),
                note.getFolder().getName(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
