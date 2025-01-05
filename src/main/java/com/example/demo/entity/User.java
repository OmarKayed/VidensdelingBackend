package com.example.demo.entity;

import com.example.demo.enums.Department;
import com.example.demo.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    public User(String username, String password, String email, Department department, UserType userType) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.department = department;
        this.userType = userType;
    }

    public User() {}
}