package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.enums.Department;
import com.example.demo.enums.UserType;
import com.example.demo.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
public class UserService {

    private final UserRepository userRepository;
    private User loggedInUser = null;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Add a new user
    public boolean addUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false; // User already exists
        }
        userRepository.save(user);
        return true;
    }

    // Login the user
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user; // Logged in successfully
            return true;
        }
        return false; // Invalid login information
    }

    // Logout the user
    public void logout() {
        loggedInUser = null; // Log out the user
    }

    // Check if the user is admin
    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getUserType().equals(UserType.ADMIN);
    }

    // Get all users
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // Delete user
    public boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            userRepository.delete(user);
            return true; // Successfully deleted
        }
        return false; // User not found
    }

    // Edit user
    public boolean editUser(String username, String newUsername, String newEmail, String newUserType, String newDepartment) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setUsername(newUsername != null ? newUsername : user.getUsername());
            user.setEmail(newEmail != null ? newEmail : user.getEmail());
            user.setUserType(newUserType != null ? UserType.valueOf(newUserType) : user.getUserType());
            user.setDepartment(newDepartment != null ? Department.valueOf(newDepartment) : user.getDepartment());
            userRepository.save(user);
            return true; // Successfully updated
        }
        return false; // User not found
    }
}