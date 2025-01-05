package com.example.demo.controller;

import com.example.demo.dto.UserConverter;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;

    @Autowired
    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO loginRequest) {
        if (!userService.login(loginRequest.username(), loginRequest.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Forkert brugernavn eller password");
        }
        return ResponseEntity.ok("Logged ind!");
    }

    // Logout Endpoint
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout();
        return ResponseEntity.ok("Logged ud");
    }

    // Get Current Logged-In User
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User loggedInUser = userService.getLoggedInUser();

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UserDTO userDTO = userConverter.toDTO(loggedInUser);
        return ResponseEntity.ok(userDTO);
    }

    // Get All Users
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        if (!userService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Restrict access to admins
        }

        List<User> users = userService.getUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Add User
    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@RequestBody UserDTO userDTO) {
        if (!userService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Kun admin can tilføje brugere");
        }

        User user = userConverter.toEntity(userDTO);
        boolean userAdded = userService.addUser(user);

        if (userAdded) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Bruger tilføjet");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Denne bruger eksisterer allerede");
        }
    }

    // Delete a user
    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO userDTO) {
        if (!userService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Kun admins kan fjerne brugeren");
        }

        boolean deleted = userService.deleteUser(userDTO.username());
        if (deleted) {
            return ResponseEntity.ok("Bruger er blevet slettet");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Brugeren blev ikke fundet");
        }
    }

    // Edit a user
    @PutMapping("/edit-user")
    public ResponseEntity<String> editUser(@RequestBody UserDTO userDTO) {
        if (!userService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Kun admin kan rette brugerne");
        }

        boolean updated = userService.editUser(
                userDTO.username(),
                userDTO.newUsername(),
                userDTO.email(),
                userDTO.userType(),
                userDTO.department()
        );

        if (updated) {
            return ResponseEntity.ok("Brugeren blev opdateret");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Brugeren blev ikke fundet");
        }
    }
}