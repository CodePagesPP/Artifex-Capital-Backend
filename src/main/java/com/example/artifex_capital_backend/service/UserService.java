package com.example.artifex_capital_backend.service;

import com.example.artifex_capital_backend.dto.AdminDTO;
import com.example.artifex_capital_backend.dto.ClientRegistrationDTO;
import com.example.artifex_capital_backend.dto.UserCreateDTO;
import com.example.artifex_capital_backend.dto.UserDTO;
import com.example.artifex_capital_backend.model.Client;

import java.util.List;

public interface UserService {
    UserDTO registerAdmin(AdminDTO admin);
    UserDTO registerUser(UserCreateDTO dto);
    Client registerClient(ClientRegistrationDTO registrationDTO);
    UserDTO updateUser(long id, UserCreateDTO dto);
    void deleteUser(long id);
    List<UserDTO> getUsersByRole(String roleName);
    UserDTO getUserById(long id);
}
