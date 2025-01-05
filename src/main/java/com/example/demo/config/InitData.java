package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.enums.Department;
import com.example.demo.enums.UserType;
import com.example.demo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitData implements CommandLineRunner {

    private final UserService userService;

    public InitData(UserService userService) {
        this.userService = userService;
    }

    // Test data
    @Override
    public void run(String... args) throws Exception {
        userService.addUser(new User(
                "Admin", "password123", "admin@demo.com", Department.IT, UserType.ADMIN));
        userService.addUser(new User(
                "User", "password123", "user@demo.com", Department.ØKONOMI, UserType.USER));

        for (User user : userService.getUsers()) {
            System.out.println(" - " + user.getUsername() + " (Department: " + user.getDepartment() + ", Type: " + user.getUserType() + ")");
        }
    }
}