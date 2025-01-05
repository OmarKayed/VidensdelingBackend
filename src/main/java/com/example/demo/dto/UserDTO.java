package com.example.demo.dto;

import com.example.demo.enums.Department;
import com.example.demo.enums.UserType;

public record UserDTO(String username, String newUsername, String email, String userType, String department, String password) {

}
