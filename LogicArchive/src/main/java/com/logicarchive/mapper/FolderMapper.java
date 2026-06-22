package com.logicarchive.mapper;

import com.logicarchive.dto.request.FolderCreateRequest;
import com.logicarchive.dto.response.FolderResponse;
import com.logicarchive.entity.Folder;
import com.logicarchive.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FolderMapper {

    public Folder toEntity(FolderCreateRequest request, User user) {
        return Folder.builder()
                .name(request.name())
                .description(request.description())
                .user(user)
                .build();
    }

    public FolderResponse toResponse(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getName(),
                folder.getDescription(),
                folder.getUser().getId(),
                folder.getUser().getName(),
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }
}
