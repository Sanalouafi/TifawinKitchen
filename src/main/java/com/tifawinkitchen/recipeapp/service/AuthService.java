package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.JwtResponse;
import com.tifawinkitchen.recipeapp.dto.LoginDto;
import com.tifawinkitchen.recipeapp.dto.UserRegistrationDto;
import com.tifawinkitchen.recipeapp.exception.AppException;
import com.tifawinkitchen.recipeapp.model.User;

public interface AuthService {
    User registerUser(UserRegistrationDto registrationDto) throws AppException;
    JwtResponse authenticateUser(LoginDto loginDto) throws AppException;
}