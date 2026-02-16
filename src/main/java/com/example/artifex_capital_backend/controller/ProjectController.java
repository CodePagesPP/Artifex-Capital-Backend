package com.example.artifex_capital_backend.controller;

import com.example.artifex_capital_backend.dto.ProjectDTO;
import com.example.artifex_capital_backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProjectDTO> createProject(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("progress") Integer progress,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        ProjectDTO newProject = projectService.createProject(title, description, city, country, progress, images);
        return ResponseEntity.ok(newProject);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("progress") Integer progress,
            @RequestParam("status") String status,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        ProjectDTO updatedProject = projectService.updateProject(id, title, description, city, country, progress, status, images);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ProjectDTO> deleteImage(@PathVariable Long id, @RequestParam("imageUrl") String imageUrl) {
        ProjectDTO updatedProject = projectService.deleteImage(id, imageUrl);
        return ResponseEntity.ok(updatedProject);
    }
}
