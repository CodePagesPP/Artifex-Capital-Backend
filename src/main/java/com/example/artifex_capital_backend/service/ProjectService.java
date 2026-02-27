package com.example.artifex_capital_backend.service;

import com.example.artifex_capital_backend.dto.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(String title, String description, String city, String country, Integer progress, List<MultipartFile> images) throws IOException;

    ProjectDTO getProjectById(Long id);

    List<ProjectDTO> getAllProjects();

    ProjectDTO updateProject(Long id, String title, String description, String city, String country, Integer progress, String status, List<MultipartFile> newImages) throws IOException;
    Page<ProjectDTO> getPaginatedProjects(String search, String status, int page, int size);
    void deleteProject(Long id);
    List<ProjectDTO> getProjectsInProgress();
    ProjectDTO deleteImage(Long projectId, String imageUrl);
}