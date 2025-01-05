package com.example.demo;

import com.example.demo.controller.UserController;
import com.example.demo.dto.UserConverter;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.Department;
import com.example.demo.enums.UserType;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserConverter userConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        UserDTO loginRequest = new UserDTO("user", null, null, null, null, "password");

        when(userService.login("user", "password")).thenReturn(true);

        ResponseEntity<String> response = userController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged ind!", response.getBody());
    }

    @Test
    void testLoginFailure() {
        UserDTO loginRequest = new UserDTO("user", null, null, null, null, "wrongpassword");

        when(userService.login("user", "wrongpassword")).thenReturn(false);

        ResponseEntity<String> response = userController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Forkert brugernavn eller password", response.getBody());
    }

    @Test
    void testAddUserSuccess() {
        UserDTO userDTO = new UserDTO("newUser", null, "newuser@google.com", "USER", "IT", "password");
        User user = new User("newUser", "password", "newuser@google.com", Department.IT, UserType.USER);

        when(userService.isAdmin()).thenReturn(true);
        when(userConverter.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userService.addUser(user)).thenReturn(true);

        ResponseEntity<String> response = userController.addUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Bruger tilføjet", response.getBody());
    }

    @Test
    void testAddUserFailure() {
        UserDTO userDTO = new UserDTO("existingUser", null, "existing@google.com", "USER", "IT", "password");
        User user = new User("existingUser", "password", "existing@google.com", Department.IT, UserType.USER);

        when(userService.isAdmin()).thenReturn(true);
        when(userConverter.toEntity(any(UserDTO.class))).thenReturn(user);
        when(userService.addUser(user)).thenReturn(false);

        ResponseEntity<String> response = userController.addUser(userDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Denne bruger eksisterer allerede", response.getBody());
    }

    @Test
    void testDeleteUserSuccess() {
        UserDTO userDTO = new UserDTO("user1", null, null, null, null, null);

        when(userService.isAdmin()).thenReturn(true);
        when(userService.deleteUser("user1")).thenReturn(true);

        ResponseEntity<String> response = userController.deleteUser(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bruger er blevet slettet", response.getBody());
    }

    @Test
    void testDeleteUserFailure() {
        UserDTO userDTO = new UserDTO("noUser", null, null, null, null, null);

        when(userService.isAdmin()).thenReturn(true);
        when(userService.deleteUser("noUser")).thenReturn(false);

        ResponseEntity<String> response = userController.deleteUser(userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Brugeren blev ikke fundet", response.getBody());
    }

    @Test
    void testEditUserSuccess() {
        UserDTO userDTO = new UserDTO("user1", "newUser1", "newemail@google.com", "ADMIN", "ØKONOMI", null);

        when(userService.isAdmin()).thenReturn(true);
        when(userService.editUser("user1", "newUser1", "newemail@google.com", "ADMIN", "ØKONOMI")).thenReturn(true);

        ResponseEntity<String> response = userController.editUser(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Brugeren blev opdateret", response.getBody());
    }

    @Test
    void testEditUserFailure() {
        UserDTO userDTO = new UserDTO("noUser", "newUser1", "newemail@google.com", "ADMIN", "ØKONOMI", null);

        when(userService.isAdmin()).thenReturn(true);
        when(userService.editUser("noUser", "newUser1", "newemail@google.com", "ADMIN", "ØKONOMI")).thenReturn(false);

        ResponseEntity<String> response = userController.editUser(userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Brugeren blev ikke fundet", response.getBody());
    }

    @Test
    void testGetAllUsersSuccess() {
        User user1 = new User("user1", "password", "user1@google.com", Department.IT, UserType.USER);
        User user2 = new User("admin1", "password", "admin1@google.com", Department.ØKONOMI, UserType.ADMIN);
        List<User> users = List.of(user1, user2);

        when(userService.isAdmin()).thenReturn(true);
        when(userService.getUsers()).thenReturn(users);
        when(userConverter.toDTO(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserDTO(user.getUsername(), null, user.getEmail(),
                    user.getUserType().name(), user.getDepartment().name(), null);
        });

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }
}