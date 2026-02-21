package com.example.artifex_capital_backend.dto;

import lombok.Data;

@Data
public class ClientProjectResponseDTO {
    private ProjectDTO project;
    private Double investedAmount;
}
