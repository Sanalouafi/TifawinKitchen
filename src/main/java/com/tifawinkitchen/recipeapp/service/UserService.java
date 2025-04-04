package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.UserDto;
import com.tifawinkitchen.recipeapp.dto.UserRegistrationDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.enums.DietType;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto getUserById(Long id) throws ResourceNotFoundException;
    UserDto getUserByEmail(String email) throws ResourceNotFoundException;
    List<UserDto> getAllUsers();
    UserDto createUser(UserRegistrationDto registrationDto);
    UserDto updateUser(Long id, UserDto userDto);
    public void deleteUser(Long id);
    UserDto updateUserPreferences(Long userId, Set<DietType> preferences) throws ResourceNotFoundException;
}