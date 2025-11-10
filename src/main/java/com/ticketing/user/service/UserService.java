package com.ticketing.user.service;

import com.ticketing.user.dto.UserResponse;
import com.ticketing.user.entity.User;
import com.ticketing.user.exception.NotFoundException;
import com.ticketing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        return mapToResponse(user);
    }
    
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getRole(),
            user.getActive()
        );
    }
}

