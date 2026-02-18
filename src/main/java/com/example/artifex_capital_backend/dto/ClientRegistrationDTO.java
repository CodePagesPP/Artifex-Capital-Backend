package com.example.artifex_capital_backend.dto;

import lombok.Data;

@Data
public class ClientRegistrationDTO {

    private String name;
    private String lastName;
    private String email;
    private String password;
    private String sex;
    private String phoneNumber;
    private String country;
    private String city;
    private Long projectId;
    private Double plannedInvestment;
}
