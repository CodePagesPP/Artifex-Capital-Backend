package com.example.artifex_capital_backend.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
