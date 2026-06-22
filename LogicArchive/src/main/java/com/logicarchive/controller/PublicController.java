package com.logicarchive.controller;

import com.logicarchive.dto.response.ApiResponse;
import com.logicarchive.dto.response.NoteResponse;
import com.logicarchive.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final NoteService noteService;

    @GetMapping("/notes")
    public ResponseEntity<ApiResponse> getPublicNotes() {
        List<NoteResponse> notes = noteService.getPublicNotes();
        return ResponseEntity.ok(ApiResponse.success("Public notes retrieved", notes));
    }
}
