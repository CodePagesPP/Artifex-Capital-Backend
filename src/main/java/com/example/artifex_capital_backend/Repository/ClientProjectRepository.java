package com.example.artifex_capital_backend.Repository;

import com.example.artifex_capital_backend.model.ClientProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientProjectRepository extends JpaRepository<ClientProject, Long> {
    List<ClientProject> findByClientId(Long clientId);
}
