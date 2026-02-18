package com.example.artifex_capital_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
}
