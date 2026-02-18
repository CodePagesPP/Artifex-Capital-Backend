package com.example.artifex_capital_backend.service.impl;

import com.example.artifex_capital_backend.Repository.UserRepository;
import com.example.artifex_capital_backend.model.Client;
import com.example.artifex_capital_backend.model.User;
import com.example.artifex_capital_backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository; // 1. Inyectamos el repositorio

    @Async // Importante para que el bucle no congele la respuesta al usuario
    public void sendNewClientNotification(Client client) {

        // 2. Buscamos TODOS los administradores
        List<User> admins = userRepository.findByRole_Name("ADMIN");

        if (admins.isEmpty()) {
            System.out.println("⚠️ No se encontraron administradores para notificar.");
            return;
        }

        // 3. Preparamos el contenido una sola vez (para eficiencia)
        String subject = "🚀 Nuevo Inversionista: " + client.getName();
        String htmlContent = buildEmailContent(client);

        // 4. Iteramos y enviamos
        for (User admin : admins) {
            try {
                // Validación extra por si el admin no tiene email
                if (admin.getEmail() == null || admin.getEmail().isEmpty()) continue;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom("noreply@artifex.com");
                helper.setTo(admin.getEmail()); // Aquí usamos el email de CADA admin
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                System.out.println("✅ Notificación enviada al admin: " + admin.getEmail());

            } catch (MessagingException e) {
                System.err.println("❌ Error enviando a " + admin.getEmail() + ": " + e.getMessage());
                // Continuamos con el siguiente admin aunque este falle
            }
        }
    }


    private String buildEmailContent(Client client) {
        return """
            <html>
            <body>
                <h2 style="color: #0F2B3F;">Nuevo Registro de Inversionista</h2>
                <p>Un nuevo cliente se ha registrado en la plataforma.</p>
                
                <h3>Datos del Cliente:</h3>
                <ul>
                    <li><strong>Nombre:</strong> %s %s</li>
                    <li><strong>Email:</strong> %s</li>
                    <li><strong>Teléfono:</strong> %s</li>
                    <li><strong>País/Ciudad:</strong> %s, %s</li>
                </ul>

                <h3>Interés de Inversión:</h3>
                <ul>
                    <li><strong>Proyecto:</strong> %s</li>
                    <li><strong>Monto Planeado:</strong> $%s</li>
                </ul>
            </body>
            </html>
            """.formatted(
                client.getName(), client.getLastName(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getCity(), client.getCountry(),
                client.getProjectOfInterest().getTitle(),
                client.getPlannedInvestment()
        );
    }
}