package com.logicarchive.service;

import com.logicarchive.dto.LoginRequest;
import com.logicarchive.dto.LoginResponse;
import com.logicarchive.dto.RegisterRequest;
import com.logicarchive.entity.User;
import com.logicarchive.enums.Role;
import com.logicarchive.exception.DuplicateEmailException;
import com.logicarchive.exception.InvalidCredentialsException;
import com.logicarchive.repository.UserRepository;
import com.logicarchive.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }
            User user = new User();

            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.USER);

            userRepository.save(user);

    }
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}
