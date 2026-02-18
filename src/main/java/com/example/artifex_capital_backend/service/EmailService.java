package com.example.artifex_capital_backend.service;

import com.example.artifex_capital_backend.model.Client;

public interface EmailService {
    void sendNewClientNotification(Client client);
}
