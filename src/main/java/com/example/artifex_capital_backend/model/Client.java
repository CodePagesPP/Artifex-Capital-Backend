package com.example.artifex_capital_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Client extends User {

    private String phoneNumber;
    private String country;
    private String city;

    private Double plannedInvestment;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project projectOfInterest;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientProject> assignedProjects = new ArrayList<>();
}
