package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.UserDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.impl.UserServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUtil mapperUtil;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_Success() throws ResourceNotFoundException {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Add this mock
        UserDto mockDto = new UserDto();
        mockDto.setEmail(user.getEmail());
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(mockDto);

        UserDto userDto = userService.getUserById(1L);

        assertNotNull(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUserPreferences_Success() throws ResourceNotFoundException {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Mock the mapper to return a UserDto
        UserDto mockDto = new UserDto();
        mockDto.setEmail(user.getEmail());
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(mockDto);

        UserDto updatedUser = userService.updateUserPreferences(1L, Set.of());

        assertNotNull(updatedUser);
        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }}