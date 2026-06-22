package com.logicarchive.service;

import com.logicarchive.dto.request.NoteCreateRequest;
import com.logicarchive.dto.request.NoteUpdateRequest;
import com.logicarchive.dto.response.NoteResponse;
import com.logicarchive.entity.Folder;
import com.logicarchive.entity.Note;
import com.logicarchive.entity.User;
import com.logicarchive.enums.Visibility;
import com.logicarchive.exception.DuplicateResourceException;
import com.logicarchive.exception.InvalidFileTypeException;
import com.logicarchive.exception.ResourceNotFoundException;
import com.logicarchive.exception.UnauthorizedAccessException;
import com.logicarchive.mapper.NoteMapper;
import com.logicarchive.repository.FolderRepository;
import com.logicarchive.repository.NoteRepository;
import com.logicarchive.repository.UserRepository;
import com.logicarchive.storage.FileStorageService;
import com.logicarchive.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final FileStorageService fileStorageService;
    private final NoteMapper noteMapper;

    public NoteResponse createNote(Long userId, NoteCreateRequest request,
                                    MultipartFile codeFile, MultipartFile resourceFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Folder folder = folderRepository.findByIdAndUserId(request.folderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", request.folderId()));

        if (noteRepository.existsByTitleAndFolderIdAndIsDeletedFalse(request.title(), folder.getId())) {
            throw new DuplicateResourceException(
                    "Note with title '" + request.title() + "' already exists in folder '" + folder.getName() + "'");
        }

        validateCodeFile(codeFile);
        String codeFilePath = fileStorageService.uploadCodeFile(codeFile);

        String resourceFilePath = null;
        String resourceFileName = null;
        Long resourceFileSize = null;
        if (resourceFile != null && !resourceFile.isEmpty()) {
            validateResourceFile(resourceFile);
            resourceFilePath = fileStorageService.uploadResourceFile(resourceFile);
            resourceFileName = resourceFile.getOriginalFilename();
            resourceFileSize = resourceFile.getSize();
        }

        Note note = Note.builder()
                .title(request.title())
                .description(request.description())
                .codeFileName(codeFile.getOriginalFilename())
                .codeFilePath(codeFilePath)
                .codeFileSize(codeFile.getSize())
                .resourceFileName(resourceFileName)
                .resourceFilePath(resourceFilePath)
                .resourceFileSize(resourceFileSize)
                .visibility(request.visibility() != null ? request.visibility() : Visibility.PRIVATE)
                .user(user)
                .folder(folder)
                .build();

        Note savedNote = noteRepository.save(note);
        log.info("Note created: '{}' in folder '{}' by user ID: {}", savedNote.getTitle(), folder.getName(), userId);

        return noteMapper.toResponse(savedNote);
    }

    public NoteResponse updateNote(Long userId, Long noteId, NoteUpdateRequest request) {
        Note note = noteRepository.findByIdAndUserIdAndIsDeletedFalse(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (request.title() != null && !request.title().equals(note.getTitle())) {
            if (noteRepository.existsByTitleAndFolderIdExcludingNoteId(
                    request.title(), note.getFolder().getId(), noteId)) {
                throw new DuplicateResourceException(
                        "Note with title '" + request.title() + "' already exists in this folder");
            }
            note.setTitle(request.title());
        }

        if (request.description() != null) {
            note.setDescription(request.description());
        }

        if (request.visibility() != null) {
            note.setVisibility(request.visibility());
        }

        Note updatedNote = noteRepository.save(note);
        log.info("Note updated: ID={} by user ID: {}", noteId, userId);

        return noteMapper.toResponse(updatedNote);
    }

    @Transactional(readOnly = true)
    public NoteResponse getNoteById(Long noteId, Long requestingUserId) {
        Note note = noteRepository.findByIdAndIsDeletedFalse(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (note.getVisibility() == Visibility.PRIVATE && !note.getUser().getId().equals(requestingUserId)) {
            throw new UnauthorizedAccessException("You do not have access to this private note");
        }

        return noteMapper.toResponse(note);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getNotesByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return noteRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getNotesByFolder(Long folderId, Long userId) {
        if (folderRepository.findByIdAndUserId(folderId, userId).isEmpty()) {
            throw new ResourceNotFoundException("Folder", "id", folderId);
        }

        return noteRepository.findByFolderIdAndIsDeletedFalse(folderId).stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource downloadFile(Long noteId, String fileType, Long requestingUserId) {
        Note note = noteRepository.findByIdAndIsDeletedFalse(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (note.getVisibility() == Visibility.PRIVATE && !note.getUser().getId().equals(requestingUserId)) {
            throw new UnauthorizedAccessException("You do not have access to download this file");
        }

        String filePath;
        if ("code".equalsIgnoreCase(fileType)) {
            filePath = note.getCodeFilePath();
        } else if ("resource".equalsIgnoreCase(fileType)) {
            if (note.getResourceFilePath() == null) {
                throw new ResourceNotFoundException("Resource file not found for this note");
            }
            filePath = note.getResourceFilePath();
        } else {
            throw new IllegalArgumentException("Invalid file type. Use 'code' or 'resource'");
        }

        return fileStorageService.downloadFile(filePath);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> searchNotes(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getNotesByUser(userId);
        }

        return noteRepository.searchByTitleOrCodeFileName(userId, keyword.trim()).stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public NoteResponse moveToTrash(Long userId, Long noteId) {
        Note note = noteRepository.findByIdAndUserIdAndIsDeletedFalse(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        note.setIsDeleted(true);
        note.setDeletedAt(LocalDateTime.now());

        Note trashedNote = noteRepository.save(note);
        log.info("Note moved to trash: ID={} by user ID: {}", noteId, userId);

        return noteMapper.toResponse(trashedNote);
    }

    public NoteResponse restoreNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (!note.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only restore your own notes");
        }

        if (!note.getIsDeleted()) {
            throw new IllegalArgumentException("Note is not in trash");
        }

        if (noteRepository.existsByTitleAndFolderIdAndIsDeletedFalse(note.getTitle(), note.getFolder().getId())) {
            throw new DuplicateResourceException(
                    "Cannot restore: a note with title '" + note.getTitle() + "' already exists in the folder");
        }

        note.setIsDeleted(false);
        note.setDeletedAt(null);

        Note restoredNote = noteRepository.save(note);
        log.info("Note restored: ID={} by user ID: {}", noteId, userId);

        return noteMapper.toResponse(restoredNote);
    }

    public void permanentDelete(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (!note.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only permanently delete your own notes");
        }

        if (note.getCodeFilePath() != null) {
            fileStorageService.deleteFile(note.getCodeFilePath());
        }
        if (note.getResourceFilePath() != null) {
            fileStorageService.deleteFile(note.getResourceFilePath());
        }

        noteRepository.delete(note);
        log.info("Note permanently deleted: ID={} by user ID: {}", noteId, userId);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getTrashNotes(Long userId) {
        return noteRepository.findByUserIdAndIsDeletedTrue(userId).stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public NoteResponse changeVisibility(Long userId, Long noteId, Visibility visibility) {
        Note note = noteRepository.findByIdAndUserIdAndIsDeletedFalse(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        note.setVisibility(visibility);
        Note updatedNote = noteRepository.save(note);
        log.info("Note visibility changed to {} for ID={} by user ID: {}", visibility, noteId, userId);

        return noteMapper.toResponse(updatedNote);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getPublicNotes() {
        return noteRepository.findByVisibilityAndIsDeletedFalse(Visibility.PUBLIC).stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllNotes() {
        return noteRepository.findByIsDeletedFalse().stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAllTrashNotes() {
        return noteRepository.findByIsDeletedTrue().stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void validateCodeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Code file is required");
        }
        if (!FileValidationUtil.isValidFileSize(file.getSize())) {
            throw new IllegalArgumentException("Code file size exceeds maximum limit of 20 MB");
        }
        if (!FileValidationUtil.isValidCodeFile(file.getOriginalFilename())) {
            throw new InvalidFileTypeException(
                    "Invalid code file type. Allowed: " + FileValidationUtil.ALLOWED_CODE_EXTENSIONS);
        }
    }

    private void validateResourceFile(MultipartFile file) {
        if (!FileValidationUtil.isValidFileSize(file.getSize())) {
            throw new IllegalArgumentException("Resource file size exceeds maximum limit of 20 MB");
        }
        if (!FileValidationUtil.isValidResourceFile(file.getOriginalFilename())) {
            throw new InvalidFileTypeException(
                    "Invalid resource file type. Allowed: " + FileValidationUtil.ALLOWED_RESOURCE_EXTENSIONS);
        }
    }
}
