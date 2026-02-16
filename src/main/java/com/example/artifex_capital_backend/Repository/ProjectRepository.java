package com.example.artifex_capital_backend.Repository;

import com.example.artifex_capital_backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
