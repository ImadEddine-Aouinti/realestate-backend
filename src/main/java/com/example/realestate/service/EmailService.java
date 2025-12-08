package com.example.realestate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // NOUVEAU : pour les logs
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j  // NOUVEAU : active les logs
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bienvenue sur RealEstate !");
        message.setText("Bonjour " + userName + ",\n\nVotre compte a été créé avec succès. Vous pouvez maintenant vous connecter avec votre email et mot de passe.\n\nCordialement,\nL'équipe RealEstate");

        try {
            mailSender.send(message);
            log.info("Email de bienvenue envoyé avec succès à : {}", toEmail);  // NOUVEAU : log succès
        } catch (MailException e) {
            log.error("Échec envoi email à {} : {}", toEmail, e.getMessage());  // NOUVEAU : log erreur
            throw new RuntimeException("Impossible d'envoyer l'email de bienvenue", e);
        }
    }
}