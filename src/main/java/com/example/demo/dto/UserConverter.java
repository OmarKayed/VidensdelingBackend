package com.example.demo.dto;

import com.example.demo.entity.User;
import com.example.demo.enums.Department;
import com.example.demo.enums.UserType;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public User toEntity(UserDTO userDTO) {
        return new User(
                userDTO.username(),
                userDTO.password(),
                userDTO.email(),
                userDTO.department() != null ? Department.valueOf(userDTO.department()) : null,
                userDTO.userType() != null ? UserType.valueOf(userDTO.userType()) : null
        );
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                null, // A new username is not required
                user.getEmail(),
                user.getUserType() != null ? user.getUserType().name() : null,
                user.getDepartment() != null ? user.getDepartment().name() : null,
                user.getPassword()
        );
    }
}