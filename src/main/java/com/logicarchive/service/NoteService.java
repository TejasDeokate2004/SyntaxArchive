package com.logicarchive.service;

import com.logicarchive.dto.NoteRequest;
import com.logicarchive.dto.NoteResponse;
import com.logicarchive.entity.Folder;
import com.logicarchive.entity.Note;
import com.logicarchive.entity.User;
import com.logicarchive.enums.Visibility;
import com.logicarchive.exception.ResourceNotFoundException;
import com.logicarchive.repository.FolderRepository;
import com.logicarchive.repository.NoteRepository;
import com.logicarchive.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final FileStorageService fileStorageService;

    public NoteService(
            NoteRepository noteRepository,
            UserRepository userRepository,
            FolderRepository folderRepository,
            FileStorageService fileStorageService
    ) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public NoteResponse createNote(Long userId, NoteRequest request, MultipartFile codeFile, MultipartFile resourceFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Folder folder = folderRepository.findByIdAndUserId(request.getFolderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Folder not found with id: " + request.getFolderId()));

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        note.setVisibility(request.getVisibility() != null ? request.getVisibility() : Visibility.PRIVATE);
        note.setUser(user);
        note.setFolder(folder);

        if (codeFile != null && !codeFile.isEmpty()) {
            note.setCodeFileName(fileStorageService.storeCodeFile(codeFile));
        }
        if (resourceFile != null && !resourceFile.isEmpty()) {
            note.setResourceFileName(fileStorageService.storeResourceFile(resourceFile));
        }

        Note saved = noteRepository.save(note);
        return new NoteResponse(saved, fileStorageService);
    }

    @Transactional
    public NoteResponse updateNote(Long userId, Long noteId, NoteRequest request, MultipartFile codeFile, MultipartFile resourceFile) {
        Note note = findNote(noteId, userId);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            note.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            note.setDescription(request.getDescription());
        }
        if (request.getVisibility() != null) {
            note.setVisibility(request.getVisibility());
        }

        if (codeFile != null && !codeFile.isEmpty()) {
            fileStorageService.delete(note.getCodeFileName());
            note.setCodeFileName(fileStorageService.storeCodeFile(codeFile));
        }
        if (resourceFile != null && !resourceFile.isEmpty()) {
            fileStorageService.delete(note.getResourceFileName());
            note.setResourceFileName(fileStorageService.storeResourceFile(resourceFile));
        }

        Note saved = noteRepository.save(note);
        return new NoteResponse(saved, fileStorageService);
    }

    public NoteResponse getNote(Long userId, Long noteId) {
        return new NoteResponse(findNote(noteId, userId), fileStorageService);
    }

    public List<NoteResponse> getAllNotes(Long userId) {
        return noteRepository.findByUserId(userId).stream()
                .map(note -> new NoteResponse(note, fileStorageService))
                .collect(Collectors.toList());
    }

    public List<NoteResponse> getNotesByFolder(Long userId, Long folderId) {
        return noteRepository.findByFolderIdAndUserId(folderId, userId).stream()
                .map(note -> new NoteResponse(note, fileStorageService))
                .collect(Collectors.toList());
    }

    public List<NoteResponse> searchNotes(Long userId, String keyword) {
        return noteRepository.searchByKeyword(userId, keyword).stream()
                .map(note -> new NoteResponse(note, fileStorageService))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteNote(Long userId, Long noteId) {
        Note note = findNote(noteId, userId);
        fileStorageService.delete(note.getCodeFileName());
        fileStorageService.delete(note.getResourceFileName());
        noteRepository.delete(note);
    }

    private Note findNote(Long noteId, Long userId) {
        return noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));
    }
}
