package com.logicarchive.controller;

import com.logicarchive.dto.FolderRequest;
import com.logicarchive.dto.FolderResponse;
import com.logicarchive.service.FolderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// NOTE: userId is taken as a request param for now since authentication is not
// part of Phase 1. Once security is added, this will come from the logged-in user.
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @RequestParam Long userId,
            @Valid @RequestBody FolderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(folderService.createFolder(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FolderResponse> updateFolder(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody FolderRequest request) {
        return ResponseEntity.ok(folderService.updateFolder(userId, id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderResponse> getFolder(@RequestParam Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(folderService.getFolder(userId, id));
    }

    @GetMapping
    public ResponseEntity<List<FolderResponse>> getAllFolders(@RequestParam Long userId) {
        return ResponseEntity.ok(folderService.getAllFolders(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@RequestParam Long userId, @PathVariable Long id) {
        folderService.deleteFolder(userId, id);
        return ResponseEntity.ok("Folder deleted");
    }
}
