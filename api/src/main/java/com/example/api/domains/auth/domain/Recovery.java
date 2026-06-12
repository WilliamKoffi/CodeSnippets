package com.example.api.domains.auth.domain;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public final class Recovery {

    private Recovery() {
    }

    public static void initiate(String email, UserRepository directory, JavaMailSender postbox) {
        User user = directory.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email address not found"));

        SimpleMailMessage dispatch = new SimpleMailMessage();
        dispatch.setFrom("noreply@codesnippets.com");
        dispatch.setTo(email);
        dispatch.setSubject("Reset Your Password - CodeSnippets");
        dispatch.setText("Hello " + user.name() + ",\n\n" +
                "You requested a password reset for your CodeSnippets account.\n" +
                "Please click the link below to reset your password:\n" +
                "http://localhost:4200/auth/reset-confirm?email=" + email + "\n\n" +
                "Best regards,\n" +
                "The CodeSnippets Team");

        postbox.send(dispatch);
        System.out.println("[MAILPIT] Password reset email sent to: " + email);
    }
}
