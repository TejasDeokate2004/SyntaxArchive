package com.logicarchive.controller;

import com.logicarchive.dto.LoginRequest;
import com.logicarchive.dto.LoginResponse;
import com.logicarchive.dto.RegisterRequest;
import com.logicarchive.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class        AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return new ResponseEntity<>("User Registered Successfully", HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
