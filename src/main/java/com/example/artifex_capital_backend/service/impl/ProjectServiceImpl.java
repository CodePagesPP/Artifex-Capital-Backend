package com.example.artifex_capital_backend.service.impl;

import com.example.artifex_capital_backend.Repository.ProjectRepository;
import com.example.artifex_capital_backend.dto.ProjectDTO;
import com.example.artifex_capital_backend.model.Project;
import com.example.artifex_capital_backend.service.ProjectService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    private final String UPLOAD_DIR = "uploads/";

    @PostConstruct
    private void createUploadDirectory() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public ProjectDTO createProject(String title, String description, String city, String country, Integer progress, List<MultipartFile> images) throws IOException {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setCity(city);
        project.setCountry(country);
        project.setProgress(progress);
        project.setStatus("IN_PROGRESS");

        String photoUrls = saveImages(images);
        project.setPhotoUrls(photoUrls != null ? photoUrls : "");

        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return mapToDTO(project);
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO updateProject(Long id, String title, String description, String city, String country, Integer progress, String status, List<MultipartFile> newImages) throws IOException{
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setTitle(title);
        project.setDescription(description);
        project.setCity(city);
        project.setCountry(country);
        project.setProgress(progress);
        project.setStatus(status);

        if (newImages != null && !newImages.isEmpty()) {
            String newUrls = saveImages(newImages);

            if (!newUrls.isEmpty()) {
                String currentUrls = project.getPhotoUrls();
                if (currentUrls != null && !currentUrls.trim().isEmpty()) {
                    project.setPhotoUrls(currentUrls + "," + newUrls);
                } else {
                    project.setPhotoUrls(newUrls);
                }
            }
        }

        Project updatedProject = projectRepository.save(project);
        return mapToDTO(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }

    @Override
    public ProjectDTO deleteImage(Long projectId, String imageUrl) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        String currentUrls = project.getPhotoUrls();
        if (currentUrls != null && !currentUrls.isEmpty()) {
            List<String> urlList = new ArrayList<>(Arrays.asList(currentUrls.split(",")));
            urlList.remove(imageUrl);

            project.setPhotoUrls(String.join(",", urlList));
            projectRepository.save(project);
        }

        return mapToDTO(project);
    }

    private String saveImages(List<MultipartFile> images) throws IOException {
        if (images == null || images.isEmpty()) return "";

        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());
                fileNames.add(fileName);
            }
        }
        return String.join(",", fileNames);
    }

    private ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setCity(project.getCity());
        dto.setCountry(project.getCountry());
        dto.setProgress(project.getProgress());
        dto.setStatus(project.getStatus());
        dto.setCreatedAt(project.getCreatedAt());

        if (project.getPhotoUrls() != null && !project.getPhotoUrls().isEmpty()) {
            dto.setPhotoUrls(Arrays.asList(project.getPhotoUrls().split(",")));
        } else {
            dto.setPhotoUrls(new ArrayList<>());
        }
        return dto;
    }
}
