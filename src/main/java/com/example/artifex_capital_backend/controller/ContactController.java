package com.example.artifex_capital_backend.controller;
import com.example.artifex_capital_backend.dto.ContactFormDTO;
import com.example.artifex_capital_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendContactMessage(@RequestBody ContactFormDTO contactForm) {
        try {
            emailService.sendContactFormEmail(contactForm);
            return ResponseEntity.ok(Collections.singletonMap("message", "Message sent successfully!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("message", "Failed to send message."));
        }
    }
}
