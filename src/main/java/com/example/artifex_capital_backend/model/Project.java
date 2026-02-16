package com.example.artifex_capital_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String city;
    private String country;

    private Integer progress;

    private LocalDate createdAt;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String photoUrls;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        if (this.status == null) this.status = "IN_PROGRESS";
    }
}
