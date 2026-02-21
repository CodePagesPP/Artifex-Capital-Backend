package com.example.artifex_capital_backend.service;

import com.example.artifex_capital_backend.dto.ClientResponseDTO;
import com.example.artifex_capital_backend.dto.ClientUpdateProjectsDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {
    Page<ClientResponseDTO> getAllClients(String searchTerm, int page, int size);
    ClientResponseDTO updateClientProjects(Long clientId, ClientUpdateProjectsDTO dto);
}
