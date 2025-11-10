package com.ticketing.user.service;

import com.ticketing.user.dto.AuthResponse;
import com.ticketing.user.dto.LoginRequest;
import com.ticketing.user.dto.RegisterRequest;
import com.ticketing.user.entity.User;
import com.ticketing.user.exception.BadRequestException;
import com.ticketing.user.exception.UnauthorizedException;
import com.ticketing.user.repository.UserRepository;
import com.ticketing.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setActive(true);
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        if (!user.getActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole()
        );
    }
}

