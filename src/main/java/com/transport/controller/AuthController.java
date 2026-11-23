package com.transport.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transport.entity.User;
import com.transport.repository.UserRepository;
import com.transport.service.JwtService;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, UserRepository userRepository,
                          JwtService jwtService,
                          org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
        User u = userRepository.findByUsername(req.username()).orElseThrow();
        String token = jwtService.generateToken(u.getUsername(), u.getRole());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", u.getUsername(),
                "fullName", u.getFullName() == null ? "" : u.getFullName(),
                "role", u.getRole()));
    }

    @GetMapping("/me")
    public Map<String, Object> me(org.springframework.security.core.Authentication auth) {
        return Map.of("username", auth.getName(),
                "role", auth.getAuthorities().iterator().next().getAuthority());
    }

    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(org.springframework.security.core.Authentication auth,
                                            @RequestBody ChangePasswordRequest req) {
        User u = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (!passwordEncoder.matches(req.currentPassword(), u.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Current password does not match"));
        }
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
