package com.kizora.taskmanager.service;

import com.kizora.taskmanager.domain.User;
import com.kizora.taskmanager.dto.CreateUserRequest;
import com.kizora.taskmanager.dto.UserDto;
import com.kizora.taskmanager.exception.ResourceNotFoundException;
import com.kizora.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(CreateUserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .build();
        user = userRepository.save(user);
        return toDto(user);
    }

    public UserDto getUser(Long id) {
        return toDto(findById(id));
    }

    public List<UserDto> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}
