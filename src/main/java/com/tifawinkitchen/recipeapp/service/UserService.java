package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.UserDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.enums.DietType;

import java.util.Set;

public interface UserService {
    UserDto getUserById(Long id) throws ResourceNotFoundException;
    UserDto getUserByEmail(String email) throws ResourceNotFoundException;
    UserDto updateUserPreferences(Long userId, Set<DietType> preferences) throws ResourceNotFoundException;
}