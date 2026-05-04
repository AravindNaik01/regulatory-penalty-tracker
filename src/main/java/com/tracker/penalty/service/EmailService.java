package com.tracker.penalty.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${app.admin-email:admin@example.com}")
    private String adminEmail;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendNewPenaltyAlert(String penaltyTitle, String severity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("New Penalty Created: " + penaltyTitle);
        message.setText("A new regulatory penalty has been created.\n\nTitle: " + penaltyTitle + "\nSeverity: " + severity);
        emailSender.send(message);
    }

    public void sendReminderAlert(String penaltyTitle, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("Reminder: Action required for Penalty - " + penaltyTitle);
        message.setText("This is a reminder for a penalty requiring attention.\n\nTitle: " + penaltyTitle + "\nStatus: " + status);
        emailSender.send(message);
    }
}
