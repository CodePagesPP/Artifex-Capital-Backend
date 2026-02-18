package com.example.artifex_capital_backend.service.impl;

import com.example.artifex_capital_backend.Repository.*;
import com.example.artifex_capital_backend.dto.AdminDTO;
import com.example.artifex_capital_backend.dto.ClientRegistrationDTO;
import com.example.artifex_capital_backend.dto.UserCreateDTO;
import com.example.artifex_capital_backend.dto.UserDTO;
import com.example.artifex_capital_backend.model.*;
import com.example.artifex_capital_backend.service.EmailService;
import com.example.artifex_capital_backend.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository2;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final EmailService emailService;
    @Override
    public UserDTO registerAdmin(AdminDTO admin) {
        if(adminRepository.findByEmail(admin.getEmail()).isPresent()){
            throw new EntityExistsException("User with this dni already exists");
        };

        RoleE adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no existe en la base de datos"));


        Admin user = Admin.builder()
                .email(admin.getEmail())
                .password(passwordEncoder.encode(admin.getPassword()))
                .role(adminRole)
                .name(admin.getName())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        System.out.println(user.getRole());
        return mapToDTO(adminRepository.save(user));
    }


    @Override
    public UserDTO registerUser(UserCreateDTO dto) {
        if(userRepository2.findByEmail(dto.getEmail()).isPresent()){
            throw new EntityExistsException("El usuario con este email ya existe");
        }
        RoleE roleEntity = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + dto.getRole()));
        User userToSave;
        switch (dto.getRole()) {
            case "ADMIN":
                userToSave = Admin.builder().build();
                break;

            default:
                throw new IllegalArgumentException("Tipo de rol no soportado para registro");
        }

        userToSave.setEmail(dto.getEmail());
        userToSave.setName(dto.getName());
        userToSave.setLastName(dto.getLastName());
        userToSave.setSex(dto.getSex());
        userToSave.setPassword(passwordEncoder.encode(dto.getPassword()));
        userToSave.setRole(roleEntity);
        userToSave.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        User savedUser = userRepository2.save(userToSave);

        return mapToDTO(savedUser);
    }

    @Override
    @Transactional
    public Client registerClient(ClientRegistrationDTO dto) {


        RoleE clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Error: El rol CLIENT no existe en la base de datos."));


        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Error: Proyecto no encontrado con ID: " + dto.getProjectId()));


        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Error: El correo ya está registrado.");
        }


        Client client = new Client();

        client.setName(dto.getName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPassword(passwordEncoder.encode(dto.getPassword())); // ¡Encriptamos contraseña!
        client.setSex(dto.getSex());
        client.setRole(clientRole);
        client.setCreatedAt(new Timestamp(System.currentTimeMillis()));


        client.setPhoneNumber(dto.getPhoneNumber());
        client.setCountry(dto.getCountry());
        client.setCity(dto.getCity());
        client.setPlannedInvestment(dto.getPlannedInvestment());
        client.setProjectOfInterest(project);

        Client savedClient = clientRepository.save(client);
        emailService.sendNewClientNotification(savedClient);
        return savedClient;
    }


    @Override
    public List<UserDTO> getUsersByRole(String roleName) {

        if (roleName == null || roleName.equals("ALL")) {
            return userRepository2.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        // Busca por el rol específico ("ADMIN", "OPERADOR", etc.)
        return userRepository2.findByRole_Name(roleName).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(long id, UserCreateDTO dto) {

        User userFound = userRepository2.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));

        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!userFound.getEmail().equals(dto.getEmail())) {
                if (userRepository2.findByEmail(dto.getEmail()).isPresent()) {
                    throw new EntityExistsException("Ya existe otro usuario con el email: " + dto.getEmail());
                }
                userFound.setEmail(dto.getEmail());
            }
        }


        if (dto.getName() != null) userFound.setName(dto.getName());
        if (dto.getLastName() != null) userFound.setLastName(dto.getLastName());
        if (dto.getSex() != null) userFound.setSex(dto.getSex());


        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            userFound.setPassword(passwordEncoder.encode(dto.getPassword()));
        }


        userFound.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return mapToDTO(userRepository2.save(userFound));
    }

    @Override
    public void deleteUser(long id) {
        if (!userRepository2.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository2.deleteById(id);
    }

    @Override
    public UserDTO getUserById(long id) {
        User user = userRepository2.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con el ID: " + id));

        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .lastName(user.getLastName())
                .sex(user.getSex())
                .role(user.getRole().getName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

