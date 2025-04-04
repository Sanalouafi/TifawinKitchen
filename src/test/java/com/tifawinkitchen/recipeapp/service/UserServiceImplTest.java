package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.UserDto;
import com.tifawinkitchen.recipeapp.dto.UserRegistrationDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.UserRole;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.impl.UserServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUtil mapperUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(UserRole.USER);
        user.setCulinaryPreferences(Set.of(DietType.VEGETARIAN));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setRole(UserRole.USER);
        userDto.setCulinaryPreferences(Set.of(DietType.VEGETARIAN));

        registrationDto = new UserRegistrationDto();
        registrationDto.setName("Test User");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
        registrationDto.setCulinaryPreferences(Set.of(DietType.VEGETARIAN));
    }

    @Test
    void getUserById_Success() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByEmail_Success() throws ResourceNotFoundException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_NotFound_ThrowsException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserByEmail("test@example.com"));
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.createUser(registrationDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(registrationDto));
    }

    @Test
    void updateUser_Success() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, userDto);

        assertNotNull(result);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateUserPreferences_Success() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapperUtil.mapUserToDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateUserPreferences(1L, Set.of(DietType.VEGAN));

        assertNotNull(result);
    }
}
