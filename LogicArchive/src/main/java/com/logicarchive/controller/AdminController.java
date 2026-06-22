package com.logicarchive.controller;

import com.logicarchive.dto.response.ApiResponse;
import com.logicarchive.dto.response.FolderResponse;
import com.logicarchive.dto.response.NoteResponse;
import com.logicarchive.dto.response.UserResponse;
import com.logicarchive.service.FolderService;
import com.logicarchive.service.NoteService;
import com.logicarchive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only admin endpoints. No authentication yet - restrict to ADMIN role
 * once Spring Security is added in the next phase.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final FolderService folderService;
    private final NoteService noteService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved", users));
    }

    @GetMapping("/folders")
    public ResponseEntity<ApiResponse> getAllFolders() {
        List<FolderResponse> folders = folderService.getAllFolders();
        return ResponseEntity.ok(ApiResponse.success("All folders retrieved", folders));
    }

    @GetMapping("/notes")
    public ResponseEntity<ApiResponse> getAllNotes() {
        List<NoteResponse> notes = noteService.getAllNotes();
        return ResponseEntity.ok(ApiResponse.success("All notes retrieved", notes));
    }

    @GetMapping("/notes/trash")
    public ResponseEntity<ApiResponse> getAllTrashNotes() {
        List<NoteResponse> notes = noteService.getAllTrashNotes();
        return ResponseEntity.ok(ApiResponse.success("All trash notes retrieved", notes));
    }
}
