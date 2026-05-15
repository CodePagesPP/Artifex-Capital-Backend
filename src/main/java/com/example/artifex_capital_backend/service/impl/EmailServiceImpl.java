package com.example.artifex_capital_backend.service.impl;

import com.example.artifex_capital_backend.Repository.UserRepository;
import com.example.artifex_capital_backend.dto.ContactFormDTO;
import com.example.artifex_capital_backend.model.Client;
import com.example.artifex_capital_backend.model.User;
import com.example.artifex_capital_backend.service.EmailService;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final Resend resend;
    private final UserRepository userRepository;

    public EmailServiceImpl(@Value("${RESEND_API_KEY}") String apiKey, UserRepository userRepository) {
        this.resend = new Resend(apiKey);
        this.userRepository = userRepository;
    }

    @Async
    @Override
    public void sendNewClientNotification(Client client) {
        List<User> admins = userRepository.findByRole_Name("ADMIN");

        if (admins.isEmpty()) {
            System.out.println("⚠️ No se encontraron administradores para notificar.");
            return;
        }

        String subject = "🚀 Nuevo Inversionista: " + client.getName();
        String htmlContent = buildEmailContent(client);

        for (User admin : admins) {
            if (admin.getEmail() == null || admin.getEmail().isEmpty()) continue;
            sendMailViaResend(admin.getEmail(), subject, htmlContent);
        }
    }

    @Async
    @Override
    public void sendContactFormEmail(ContactFormDTO contactForm) {
        List<User> admins = userRepository.findByRole_Name("ADMIN");

        if (admins.isEmpty()) return;

        String subject = "📩 Nuevo mensaje de contacto: " + contactForm.getFirstName() + " " + contactForm.getLastName();
        String htmlContent = buildContactEmailContent(contactForm);

        for (User admin : admins) {
            if (admin.getEmail() == null || admin.getEmail().isEmpty()) continue;
            sendMailViaResend(admin.getEmail(), subject, htmlContent);
        }
    }

    private void sendMailViaResend(String to, String subject, String html) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Artifex Capital <soporte@artifexcapitalpartners.com>")
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            resend.emails().send(params);
            System.out.println("✅ Correo enviado exitosamente vía Resend API a: " + to);
        } catch (Exception e) {
            System.err.println("❌ Error enviando vía Resend a " + to + ": " + e.getMessage());
        }
    }

    private String buildEmailContent(Client client) {
        String projectName = client.getProjectOfInterest() != null
                ? client.getProjectOfInterest().getTitle()
                : "No specific project";

        return """
        <html>
        <body style="margin: 0; padding: 0; background-color: #f4f5f7;">
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f5f7; padding: 40px 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                    <div style="background-color: #0F2B3F; padding: 25px; text-align: center;">
                        <h2 style="margin: 0; color: #ffffff; font-size: 22px; letter-spacing: 1px;">New Investor Registration</h2>
                    </div>
                    <div style="padding: 35px; color: #333333; font-size: 15px; line-height: 1.6;">
                        <p style="margin-top: 0;">Hello Admin,</p>
                        <p>A new client has successfully registered on the platform. Here are the details of the prospect:</p>
                        <h3 style="color: #0F2B3F; border-bottom: 2px solid #F83C3C; padding-bottom: 8px; margin-top: 30px; font-size: 18px;">Client Information</h3>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 15px;">
                            <tr><td style="padding: 8px 0; width: 120px;"><strong>Name:</strong></td><td style="padding: 8px 0; color: #555;">%s %s</td></tr>
                            <tr><td style="padding: 8px 0;"><strong>Email:</strong></td><td style="padding: 8px 0;"><a href="mailto:%s" style="color: #F83C3C; text-decoration: none;">%s</a></td></tr>
                            <tr><td style="padding: 8px 0;"><strong>Phone:</strong></td><td style="padding: 8px 0; color: #555;">%s</td></tr>
                            <tr><td style="padding: 8px 0;"><strong>Location:</strong></td><td style="padding: 8px 0; color: #555;">%s, %s</td></tr>
                        </table>
                        <h3 style="color: #0F2B3F; border-bottom: 2px solid #F83C3C; padding-bottom: 8px; margin-top: 30px; font-size: 18px;">Investment Interest</h3>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 15px;">
                            <tr><td style="padding: 8px 0; width: 120px;"><strong>Project:</strong></td><td style="padding: 8px 0; font-weight: bold; color: #0F2B3F;">%s</td></tr>
                            <tr><td style="padding: 8px 0;"><strong>Planned Amount:</strong></td><td style="padding: 8px 0; color: #28a745; font-weight: bold;">$%s</td></tr>
                        </table>
                        <div style="text-align: center; margin: 40px 0 10px;">
                            <a href="https://www.artifexcapitalpartners.com/login" style="display: inline-block; padding: 12px 30px; background-color: #0F2B3F; color: #ffffff; text-decoration: none; border-radius: 6px; font-weight: bold;">View in Admin Panel</a>
                        </div>
                    </div>
                    <div style="background-color: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #999999; border-top: 1px solid #eeeeee;"><p style="margin: 0;">Artifex Capital &bull; Automated Admin Notification</p></div>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                client.getName(), client.getLastName(),
                client.getEmail(), client.getEmail(),
                client.getPhoneNumber(),
                client.getCity(), client.getCountry(),
                projectName,
                client.getPlannedInvestment()
        );
    }

    private String buildContactEmailContent(ContactFormDTO form) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <div style="max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden;">
                <div style="background-color: #0F2B3F; color: white; padding: 20px; text-align: center;"><h2>New Contact Inquiry</h2></div>
                <div style="padding: 20px;">
                    <p><strong>From:</strong> %s %s</p>
                    <p><strong>Email:</strong> %s</p>
                    <p><strong>Phone:</strong> %s</p>
                    <hr style="border: 0; border-top: 1px solid #eee;">
                    <p><strong>Message:</strong></p>
                    <p style="background: #f9f9f9; padding: 15px; border-left: 4px solid #F83C3C;">%s</p>
                </div>
                <div style="background: #f4f4f4; padding: 10px; text-align: center; font-size: 12px;">This message was sent from the Artifex Contact Page.</div>
            </div>
        </body>
        </html>
        """.formatted(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPhone(), form.getMessage());
    }
}