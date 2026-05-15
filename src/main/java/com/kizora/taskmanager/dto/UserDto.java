package com.kizora.taskmanager.dto;

import com.kizora.taskmanager.domain.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
}
