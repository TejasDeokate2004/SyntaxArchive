package com.logicarchive.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logicarchive.dto.NoteRequest;
import com.logicarchive.dto.NoteResponse;
import com.logicarchive.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// NOTE: userId is taken as a request param for now since authentication is not
// part of Phase 1. Once security is added, this will come from the logged-in user.
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<NoteResponse> createNote(
            @RequestParam Long userId,
            @RequestPart("note") String noteJson,
            @RequestPart(value = "codeFile", required = false) MultipartFile codeFile,
            @RequestPart(value = "resourceFile", required = false) MultipartFile resourceFile) throws IOException {

        NoteRequest request = objectMapper.readValue(noteJson, NoteRequest.class);
        NoteResponse response = noteService.createNote(userId, request, codeFile, resourceFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<NoteResponse> updateNote(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestPart("note") String noteJson,
            @RequestPart(value = "codeFile", required = false) MultipartFile codeFile,
            @RequestPart(value = "resourceFile", required = false) MultipartFile resourceFile) throws IOException {

        NoteRequest request = objectMapper.readValue(noteJson, NoteRequest.class);
        NoteResponse response = noteService.updateNote(userId, id, request, codeFile, resourceFile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(@RequestParam Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNote(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(@RequestParam Long userId) {
        return ResponseEntity.ok(noteService.getAllNotes(userId));
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<NoteResponse>> getNotesByFolder(
            @RequestParam Long userId, @PathVariable Long folderId) {
        return ResponseEntity.ok(noteService.getNotesByFolder(userId, folderId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotes(
            @RequestParam Long userId, @RequestParam String keyword) {
        return ResponseEntity.ok(noteService.searchNotes(userId, keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(@RequestParam Long userId, @PathVariable Long id) {
        noteService.deleteNote(userId, id);
        return ResponseEntity.ok("Note deleted");
    }
}
