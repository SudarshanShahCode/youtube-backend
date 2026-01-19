package com.youtube.controllers;

import com.youtube.dtos.AuthResponse;
import com.youtube.dtos.LoginRequest;
import com.youtube.dtos.UserDto;
import com.youtube.dtos.VerifyUserDto;
import com.youtube.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(201).body(userService.register(userDto));
    }

    @PostMapping("/verify-user")
    public ResponseEntity<Map<String, Object>> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto) {
        return ResponseEntity.ok(Map.of("message", "User verified successfully",
                                        "isVerified", userService.verifyUser(verifyUserDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }
}
