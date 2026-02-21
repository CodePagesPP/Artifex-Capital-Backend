package com.example.artifex_capital_backend.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "client_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "invested_amount")
    private Double investedAmount;
}
