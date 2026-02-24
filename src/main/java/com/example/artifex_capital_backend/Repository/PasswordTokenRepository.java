package com.example.artifex_capital_backend.Repository;

import com.example.artifex_capital_backend.model.PasswordResetToken;
import com.example.artifex_capital_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
