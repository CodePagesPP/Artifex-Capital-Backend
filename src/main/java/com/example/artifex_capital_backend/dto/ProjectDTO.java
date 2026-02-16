package com.example.artifex_capital_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private String city;
    private String country;
    private Integer progress;
    private String status;
    private LocalDate createdAt;
    private List<String> photoUrls;
}
