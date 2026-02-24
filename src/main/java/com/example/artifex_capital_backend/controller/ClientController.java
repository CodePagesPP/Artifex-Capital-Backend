package com.example.artifex_capital_backend.controller;

import com.example.artifex_capital_backend.Repository.ClientRepository;
import com.example.artifex_capital_backend.dto.ClientProjectResponseDTO;
import com.example.artifex_capital_backend.dto.ClientResponseDTO;
import com.example.artifex_capital_backend.dto.ClientUpdateProjectsDTO;
import com.example.artifex_capital_backend.model.Client;
import com.example.artifex_capital_backend.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @GetMapping
    public ResponseEntity<Page<ClientResponseDTO>> getAllClients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(clientService.getAllClients(search, page, size));
    }

    @PutMapping("/{id}/projects")
    public ResponseEntity<ClientResponseDTO> updateClientProjects(
            @PathVariable Long id,
            @RequestBody ClientUpdateProjectsDTO updateDTO) {

        ClientResponseDTO updatedClient = clientService.updateClientProjects(id, updateDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/me/projects")
    public ResponseEntity<List<ClientProjectResponseDTO>> getMyProjects(Authentication authentication) {
        String email = authentication.getName();

        Long clientId = clientRepository.findIdByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        List<ClientProjectResponseDTO> myProjects = clientService.getMyAssignedProjects(clientId);
        return ResponseEntity.ok(myProjects);
    }
}
