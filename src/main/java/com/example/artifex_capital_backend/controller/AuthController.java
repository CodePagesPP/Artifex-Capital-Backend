package com.example.artifex_capital_backend.controller;

import com.example.artifex_capital_backend.Repository.UserRepository;
import com.example.artifex_capital_backend.auth.AuthRequest;
import com.example.artifex_capital_backend.auth.AuthResponse;
import com.example.artifex_capital_backend.dto.*;
import com.example.artifex_capital_backend.model.Client;
import com.example.artifex_capital_backend.model.User;
import com.example.artifex_capital_backend.service.AuthService;
import com.example.artifex_capital_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")

@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    @PostMapping("/registerAdmin")
    public ResponseEntity<UserDTO> register(@RequestBody AdminDTO request){
        return ResponseEntity.ok(userService.registerAdmin(request));
    }

    @PostMapping("/register-client")
    public ResponseEntity<?> registerClient(@RequestBody ClientRegistrationDTO clientDTO) {
        try {
            Client newClient = userService.registerClient(clientDTO);
            return ResponseEntity.ok(Collections.singletonMap("message", "Cliente registrado exitosamente con ID: " + newClient.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", "Error interno del servidor"));
        }
    }

    @PostMapping("/registerUser")
    public ResponseEntity<UserDTO> register(@RequestBody UserCreateDTO request){
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserProfileDTO dto = UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .role(user.getRole().getName())
                .build();

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){

        final String jwt = authService.login(request);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }


}

