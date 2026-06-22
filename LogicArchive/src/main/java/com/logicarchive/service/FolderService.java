package com.logicarchive.service;

import com.logicarchive.dto.request.FolderCreateRequest;
import com.logicarchive.dto.request.FolderUpdateRequest;
import com.logicarchive.dto.response.FolderResponse;
import com.logicarchive.entity.Folder;
import com.logicarchive.entity.Note;
import com.logicarchive.entity.User;
import com.logicarchive.exception.DuplicateResourceException;
import com.logicarchive.exception.ResourceNotFoundException;
import com.logicarchive.mapper.FolderMapper;
import com.logicarchive.repository.FolderRepository;
import com.logicarchive.repository.NoteRepository;
import com.logicarchive.repository.UserRepository;
import com.logicarchive.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final FolderMapper folderMapper;
    private final FileStorageService fileStorageService;

    public FolderResponse createFolder(Long userId, FolderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (folderRepository.existsByNameAndUserId(request.name(), userId)) {
            throw new DuplicateResourceException(
                    "Folder with name '" + request.name() + "' already exists for this user");
        }

        Folder folder = folderMapper.toEntity(request, user);
        Folder savedFolder = folderRepository.save(folder);
        log.info("Folder created: '{}' for user ID: {}", savedFolder.getName(), userId);

        return folderMapper.toResponse(savedFolder);
    }

    public FolderResponse updateFolder(Long userId, Long folderId, FolderUpdateRequest request) {
        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));

        if (request.name() != null && !request.name().equals(folder.getName())) {
            if (folderRepository.existsByNameAndUserId(request.name(), userId)) {
                throw new DuplicateResourceException(
                        "Folder with name '" + request.name() + "' already exists for this user");
            }
            folder.setName(request.name());
        }

        if (request.description() != null) {
            folder.setDescription(request.description());
        }

        Folder updatedFolder = folderRepository.save(folder);
        log.info("Folder updated: ID={} for user ID: {}", folderId, userId);

        return folderMapper.toResponse(updatedFolder);
    }

    public void deleteFolder(Long userId, Long folderId) {
        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));

        List<Note> notes = noteRepository.findByFolderIdAndIsDeletedFalse(folderId);
        for (Note note : notes) {
            deleteNoteFiles(note);
        }

        List<Note> trashedNotes = folder.getNotes().stream()
                .filter(Note::getIsDeleted)
                .collect(Collectors.toList());
        for (Note note : trashedNotes) {
            deleteNoteFiles(note);
        }

        folderRepository.delete(folder);
        log.info("Folder deleted: ID={} for user ID: {}", folderId, userId);
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> getFoldersByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return folderRepository.findByUserId(userId).stream()
                .map(folderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FolderResponse getFolderById(Long userId, Long folderId) {
        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", folderId));
        return folderMapper.toResponse(folder);
    }

    @Transactional(readOnly = true)
    public List<FolderResponse> getAllFolders() {
        return folderRepository.findAll().stream()
                .map(folderMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void deleteNoteFiles(Note note) {
        try {
            if (note.getCodeFilePath() != null) {
                fileStorageService.deleteFile(note.getCodeFilePath());
            }
            if (note.getResourceFilePath() != null) {
                fileStorageService.deleteFile(note.getResourceFilePath());
            }
        } catch (Exception e) {
            log.warn("Failed to delete files for note ID={}: {}", note.getId(), e.getMessage());
        }
    }
}
