package com.example.artifex_capital_backend.service.impl;

import com.example.artifex_capital_backend.Repository.PasswordTokenRepository;
import com.example.artifex_capital_backend.Repository.UserRepository;
import com.example.artifex_capital_backend.model.PasswordResetToken;
import com.example.artifex_capital_backend.model.User;
import com.example.artifex_capital_backend.service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken(token, user, 30);
        tokenRepository.save(resetToken);

        String resetUrl = "http://localhost:4200/reset-password?token=" + token;

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset Request - Artifex Capital");


            String htmlMsg = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f5f7; padding: 40px 20px;\">"
                    + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);\">"


                    + "<div style=\"background-color: #0F2B3F; padding: 25px; text-align: center;\">"
                    + "<h2 style=\"margin: 0; color: #ffffff; font-size: 24px; letter-spacing: 1px;\">Artifex Capital</h2>"
                    + "</div>"


                    + "<div style=\"padding: 35px; color: #333333; font-size: 16px; line-height: 1.6;\">"
                    + "<p style=\"margin-top: 0;\">Hello <strong>" + user.getName() + "</strong>,</p>"
                    + "<p>We received a request to reset the password for your Artifex Capital account. If you didn't make this request, you can safely ignore this email.</p>"
                    + "<p>To create a new password, click the button below:</p>"


                    + "<div style=\"text-align: center; margin: 35px 0;\">"
                    + "<a href=\"" + resetUrl + "\" style=\"display: inline-block; padding: 14px 30px; background-color: #F83C3C; color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px; box-shadow: 0 4px 6px rgba(248, 60, 60, 0.2);\">Reset Password</a>"
                    + "</div>"

                    + "<p style=\"font-size: 14px; color: #777777; margin-bottom: 0;\"><em>Note: For security reasons, this link will expire in 30 minutes.</em></p>"
                    + "</div>"


                    + "<div style=\"background-color: #f9fafb; padding: 20px; text-align: center; font-size: 13px; color: #999999; border-top: 1px solid #eeeeee;\">"
                    + "<p style=\"margin: 0;\">&copy; 2026 Artifex Capital. All rights reserved.</p>"
                    + "</div>"

                    + "</div>"
                    + "</div>";


            helper.setText(htmlMsg, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error trying to send the password reset email.", e);
        }
    }

    @Transactional
    public void updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token ha expirado. Solicita uno nuevo.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        tokenRepository.delete(resetToken);
    }
}
