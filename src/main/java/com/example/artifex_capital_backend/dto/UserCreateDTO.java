package com.example.artifex_capital_backend.dto;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String email;
    private String name;
    private String lastName;
    private String sex;
    private String password;
    private String role;
}
