package com.example.artifex_capital_backend.dto;

import lombok.Data;

@Data
public class ContactFormDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String message;
}
