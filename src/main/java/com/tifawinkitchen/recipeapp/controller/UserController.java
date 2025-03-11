package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.UserDto;
import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}/preferences")
    public ResponseEntity<UserDto> updateUserPreferences(
            @PathVariable Long userId,
            @RequestBody Set<DietType> preferences) {
        UserDto updatedUser = userService.updateUserPreferences(userId, preferences);
        return ResponseEntity.ok(updatedUser);
    }
}