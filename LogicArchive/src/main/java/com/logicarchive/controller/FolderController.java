package com.logicarchive.controller;

import com.logicarchive.dto.request.FolderCreateRequest;
import com.logicarchive.dto.request.FolderUpdateRequest;
import com.logicarchive.dto.response.ApiResponse;
import com.logicarchive.dto.response.FolderResponse;
import com.logicarchive.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<ApiResponse> createFolder(
            @PathVariable Long userId,
            @Valid @RequestBody FolderCreateRequest request) {
        FolderResponse folder = folderService.createFolder(userId, request);
        return new ResponseEntity<>(
                ApiResponse.success("Folder created successfully", folder),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<ApiResponse> updateFolder(
            @PathVariable Long userId,
            @PathVariable Long folderId,
            @Valid @RequestBody FolderUpdateRequest request) {
        FolderResponse folder = folderService.updateFolder(userId, folderId, request);
        return ResponseEntity.ok(ApiResponse.success("Folder updated successfully", folder));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<ApiResponse> deleteFolder(
            @PathVariable Long userId,
            @PathVariable Long folderId) {
        folderService.deleteFolder(userId, folderId);
        return ResponseEntity.ok(ApiResponse.success("Folder deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getFoldersByUser(@PathVariable Long userId) {
        List<FolderResponse> folders = folderService.getFoldersByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Folders retrieved successfully", folders));
    }

    @GetMapping("/{folderId}")
    public ResponseEntity<ApiResponse> getFolderById(
            @PathVariable Long userId,
            @PathVariable Long folderId) {
        FolderResponse folder = folderService.getFolderById(userId, folderId);
        return ResponseEntity.ok(ApiResponse.success("Folder retrieved successfully", folder));
    }
}
