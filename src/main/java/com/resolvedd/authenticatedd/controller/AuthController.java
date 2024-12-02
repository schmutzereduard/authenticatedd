package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.dto.LoginRequest;
import com.resolvedd.authenticatedd.dto.LoginResponse;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.service.UserService;
import com.resolvedd.authenticatedd.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<User> user = userService.findByUsername(request.getUsername());
        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.get().getUsername(), user.get().getRole());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("USER"); // Default role
        }
        userService.saveUser(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
