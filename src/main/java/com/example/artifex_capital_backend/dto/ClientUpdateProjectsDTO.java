package com.example.artifex_capital_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class    ClientUpdateProjectsDTO {
    private Long projectOfInterestId;
    private List<AssignedProjectDTO> assignedProjects;
}
