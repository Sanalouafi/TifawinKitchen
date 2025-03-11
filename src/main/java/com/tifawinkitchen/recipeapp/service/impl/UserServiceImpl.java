package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.UserDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.UserService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public UserDto getUserById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapperUtil.mapUserToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return mapperUtil.mapUserToDto(user);
    }

    @Override
    public UserDto updateUserPreferences(Long userId, Set<DietType> preferences) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setCulinaryPreferences(preferences);
        User updatedUser = userRepository.save(user);

        return mapperUtil.mapUserToDto(updatedUser);
    }
}