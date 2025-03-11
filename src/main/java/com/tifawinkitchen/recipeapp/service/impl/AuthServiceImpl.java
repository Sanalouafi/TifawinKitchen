package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.JwtResponse;
import com.tifawinkitchen.recipeapp.dto.LoginDto;
import com.tifawinkitchen.recipeapp.dto.UserRegistrationDto;
import com.tifawinkitchen.recipeapp.exception.AppException;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.model.enums.UserRole;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.security.JwtTokenProvider;
import com.tifawinkitchen.recipeapp.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) throws AppException {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new AppException("Email is already taken", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(UserRole.USER);
        user.setCulinaryPreferences(registrationDto.getCulinaryPreferences());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public JwtResponse authenticateUser(LoginDto loginDto) throws AppException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return new JwtResponse(
                jwt,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
