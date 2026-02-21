package com.example.artifex_capital_backend.controller;

import com.example.artifex_capital_backend.dto.ClientResponseDTO;
import com.example.artifex_capital_backend.dto.ClientUpdateProjectsDTO;
import com.example.artifex_capital_backend.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;


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
}
