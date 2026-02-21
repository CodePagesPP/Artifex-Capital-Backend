package com.example.artifex_capital_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String country;
    private String city;
    private Double plannedInvestment;


    private ProjectDTO projectOfInterest;
    private List<ClientProjectResponseDTO> assignedProjects;
}
