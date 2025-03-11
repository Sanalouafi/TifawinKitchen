package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.ApiResponse;
import com.tifawinkitchen.recipeapp.dto.JwtResponse;
import com.tifawinkitchen.recipeapp.dto.LoginDto;
import com.tifawinkitchen.recipeapp.dto.UserRegistrationDto;
import com.tifawinkitchen.recipeapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        authService.registerUser(registrationDto);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        JwtResponse jwtResponse = authService.authenticateUser(loginDto);
        return ResponseEntity.ok(jwtResponse);
    }
}