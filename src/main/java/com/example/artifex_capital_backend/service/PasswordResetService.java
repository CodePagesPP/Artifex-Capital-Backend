package com.example.artifex_capital_backend.service;

public interface PasswordResetService {
    void processForgotPassword(String email);
    void updatePassword(String token, String newPassword);
}
