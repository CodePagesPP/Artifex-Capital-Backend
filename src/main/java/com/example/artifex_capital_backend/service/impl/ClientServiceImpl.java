package com.example.artifex_capital_backend.service.impl;

import com.example.artifex_capital_backend.Repository.ClientProjectRepository;
import com.example.artifex_capital_backend.Repository.ClientRepository;
import com.example.artifex_capital_backend.Repository.ProjectRepository;
import com.example.artifex_capital_backend.dto.*;
import com.example.artifex_capital_backend.model.Client;
import com.example.artifex_capital_backend.model.ClientProject;
import com.example.artifex_capital_backend.model.Project;
import com.example.artifex_capital_backend.service.ClientService;
import com.example.artifex_capital_backend.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final ClientProjectRepository clientProjectRepository;
    private final ProjectService projectService;

    @Override
    public Page<ClientResponseDTO> getAllClients(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clientPage;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {

            clientPage = clientRepository.searchClients(searchTerm, pageable);
        } else {

            clientPage = clientRepository.findAll(pageable);
        }

        return clientPage.map(this::mapToClientResponseDTO);
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClientProjects(Long clientId, ClientUpdateProjectsDTO dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));


        if (dto.getProjectOfInterestId() != null) {
            Project poi = projectRepository.findById(dto.getProjectOfInterestId())
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            client.setProjectOfInterest(poi);
        } else {
            client.setProjectOfInterest(null);
        }


        client.getAssignedProjects().clear();


        if (dto.getAssignedProjects() != null) {
            for (AssignedProjectDTO assignedDTO : dto.getAssignedProjects()) {
                Project p = projectRepository.findById(assignedDTO.getProjectId())
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

                ClientProject newInvestment = ClientProject.builder()
                        .client(client)
                        .project(p)
                        .investedAmount(assignedDTO.getAmount())
                        .build();

                client.getAssignedProjects().add(newInvestment);
            }
        }

        Client updatedClient = clientRepository.save(client);
        return mapToClientResponseDTO(updatedClient);
    }

    @Override
    public List<ClientProjectResponseDTO> getMyAssignedProjects(long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<ClientProject> assignments = clientProjectRepository.findByClientId(clientId);

        return assignments.stream().map(assignment -> {
            ClientProjectResponseDTO dto = new ClientProjectResponseDTO();
            dto.setInvestedAmount(assignment.getInvestedAmount());
            dto.setProject(projectService.getProjectById(assignment.getProject().getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    private ClientResponseDTO mapToClientResponseDTO(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setLastName(client.getLastName());
        dto.setEmail(client.getEmail());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setCountry(client.getCountry());
        dto.setCity(client.getCity());
        dto.setPlannedInvestment(client.getPlannedInvestment());


        if (client.getProjectOfInterest() != null) {
            dto.setProjectOfInterest(mapProjectToDTO(client.getProjectOfInterest()));
        }


        if (client.getAssignedProjects() != null) {
            List<ClientProjectResponseDTO> assignedDTOs = client.getAssignedProjects().stream()
                    .map(cp -> {
                        ClientProjectResponseDTO cpDto = new ClientProjectResponseDTO();

                        cpDto.setProject(mapProjectToDTO(cp.getProject()));

                        cpDto.setInvestedAmount(cp.getInvestedAmount());
                        return cpDto;
                    })
                    .collect(Collectors.toList());

            dto.setAssignedProjects(assignedDTOs);
        }

        return dto;
    }

    private ProjectDTO mapProjectToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setCity(project.getCity());
        dto.setCountry(project.getCountry());
        dto.setStatus(project.getStatus());
        return dto;
    }
}
