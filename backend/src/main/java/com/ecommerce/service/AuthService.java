package com.ecommerce.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.AuthRequest;
import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserDto;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        Role role = roleRepository.findByName("MERCHANT")
            .orElseThrow(() -> new ResourceNotFoundException("Merchant role not found"));

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail().trim().toLowerCase())
            .phone(request.getPhone())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .enabled(true)
            .build();

        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateTokenForUser(saved.getEmail(), saved.getRole().getName());

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .user(UserDto.fromEntity(saved))
            .build();
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = jwtTokenProvider.generateTokenForUser(user.getEmail(), user.getRole().getName());

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .user(UserDto.fromEntity(user))
            .build();
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserDto.fromEntity(user);
    }
}
