package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {

        Optional<User> optUser = userService.findByUsername(username);
        if (optUser.isEmpty()) {
            return ResponseEntity.status(404).body("User [" + username + "] not found");
        }

        return ResponseEntity.ok(optUser.get());
    }
}
