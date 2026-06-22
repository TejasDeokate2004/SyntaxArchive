package com.logicarchive.controller;

import com.logicarchive.dto.request.NoteCreateRequest;
import com.logicarchive.dto.request.NoteUpdateRequest;
import com.logicarchive.dto.response.ApiResponse;
import com.logicarchive.dto.response.NoteResponse;
import com.logicarchive.enums.Visibility;
import com.logicarchive.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createNote(
            @PathVariable Long userId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("folderId") Long folderId,
            @RequestParam(value = "visibility", required = false) Visibility visibility,
            @RequestParam("codeFile") MultipartFile codeFile,
            @RequestParam(value = "resourceFile", required = false) MultipartFile resourceFile) {

        NoteCreateRequest request = new NoteCreateRequest(title, description, folderId, visibility);
        NoteResponse note = noteService.createNote(userId, request, codeFile, resourceFile);
        return new ResponseEntity<>(
                ApiResponse.success("Note created successfully", note),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponse> updateNote(
            @PathVariable Long userId,
            @PathVariable Long noteId,
            @RequestBody NoteUpdateRequest request) {
        NoteResponse note = noteService.updateNote(userId, noteId, request);
        return ResponseEntity.ok(ApiResponse.success("Note updated successfully", note));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse> getNoteById(
            @PathVariable Long userId,
            @PathVariable Long noteId) {
        NoteResponse note = noteService.getNoteById(noteId, userId);
        return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", note));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getNotesByUser(@PathVariable Long userId) {
        List<NoteResponse> notes = noteService.getNotesByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", notes));
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<ApiResponse> getNotesByFolder(
            @PathVariable Long userId,
            @PathVariable Long folderId) {
        List<NoteResponse> notes = noteService.getNotesByFolder(folderId, userId);
        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", notes));
    }

    @GetMapping("/{noteId}/download/{fileType}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long userId,
            @PathVariable Long noteId,
            @PathVariable String fileType) {
        Resource resource = noteService.downloadFile(noteId, fileType, userId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchNotes(
            @PathVariable Long userId,
            @RequestParam String keyword) {
        List<NoteResponse> notes = noteService.searchNotes(userId, keyword);
        return ResponseEntity.ok(ApiResponse.success("Search completed", notes));
    }

    @PutMapping("/{noteId}/trash")
    public ResponseEntity<ApiResponse> moveToTrash(
            @PathVariable Long userId,
            @PathVariable Long noteId) {
        NoteResponse note = noteService.moveToTrash(userId, noteId);
        return ResponseEntity.ok(ApiResponse.success("Note moved to trash", note));
    }

    @PutMapping("/{noteId}/restore")
    public ResponseEntity<ApiResponse> restoreNote(
            @PathVariable Long userId,
            @PathVariable Long noteId) {
        NoteResponse note = noteService.restoreNote(userId, noteId);
        return ResponseEntity.ok(ApiResponse.success("Note restored successfully", note));
    }

    @DeleteMapping("/{noteId}/permanent")
    public ResponseEntity<ApiResponse> permanentDelete(
            @PathVariable Long userId,
            @PathVariable Long noteId) {
        noteService.permanentDelete(userId, noteId);
        return ResponseEntity.ok(ApiResponse.success("Note permanently deleted"));
    }

    @GetMapping("/trash")
    public ResponseEntity<ApiResponse> getTrashNotes(@PathVariable Long userId) {
        List<NoteResponse> notes = noteService.getTrashNotes(userId);
        return ResponseEntity.ok(ApiResponse.success("Trash notes retrieved", notes));
    }

    @PutMapping("/{noteId}/visibility")
    public ResponseEntity<ApiResponse> changeVisibility(
            @PathVariable Long userId,
            @PathVariable Long noteId,
            @RequestParam Visibility visibility) {
        NoteResponse note = noteService.changeVisibility(userId, noteId, visibility);
        return ResponseEntity.ok(ApiResponse.success("Visibility changed to " + visibility, note));
    }
}
