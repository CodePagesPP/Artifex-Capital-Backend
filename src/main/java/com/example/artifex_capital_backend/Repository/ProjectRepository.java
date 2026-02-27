package com.example.artifex_capital_backend.Repository;

import com.example.artifex_capital_backend.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatusOrderByIdDesc(String status);

    @Query("SELECT p FROM Project p WHERE " +
            "(:status IS NULL OR :status = '' OR p.status = :status) AND " +
            "(:search IS NULL OR :search = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Project> searchAndFilterProjects(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable);
}
