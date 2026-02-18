package com.example.artifex_capital_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ArtifexCapitalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtifexCapitalBackendApplication.class, args);
	}

}
