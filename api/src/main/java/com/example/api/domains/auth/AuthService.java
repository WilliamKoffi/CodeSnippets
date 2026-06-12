package com.example.api.domains.auth;

import com.example.api.domains.auth.dto.LoginRequest;
import com.example.api.domains.auth.dto.RegisterRequest;
import com.example.api.domains.auth.dto.UpdateProfileRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        if (userRepository.existsByHandle(request.handle())) {
            throw new IllegalArgumentException("Handle already taken");
        }

        User user = new User(
                UUID.randomUUID().toString(),
                request.name(),
                request.handle().replace("@", ""),
                request.email(),
                hashPassword(request.password()),
                "https://api.dicebear.com/7.x/bottts/svg?seed=" + request.handle(),
                120, // Default reputation for new users
                request.role() != null ? request.role() : "fullstack",
                "Aspirant" // Default level for new users
        );

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!verifyPassword(request.password(), user.password())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public User updateProfile(String id, UpdateProfileRequest request) {
        User user = getUser(id);

        String name = request.name();
        String handle = null;
        if (request.handle() != null) {
            String newHandle = request.handle().replace("@", "");
            if (!newHandle.equals(user.handle()) && userRepository.existsByHandle(newHandle)) {
                throw new IllegalArgumentException("Handle already taken");
            }
            handle = newHandle;
        }
        String avatar = request.avatar();

        user.update(name, handle, avatar);

        if (request.role() != null) user.reassign(request.role());
        if (request.level() != null) user.promote(request.level());

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email address not found"));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@codesnippets.com");
        message.setTo(email);
        message.setSubject("Reset Your Password - CodeSnippets");
        message.setText("Hello " + user.name() + ",\n\n" +
                "You requested a password reset for your CodeSnippets account.\n" +
                "Please click the link below to reset your password:\n" +
                "http://localhost:4200/auth/reset-confirm?email=" + email + "\n\n" +
                "Best regards,\n" +
                "The CodeSnippets Team");

        mailSender.send(message);
        System.out.println("[MAILPIT] Password reset email sent to: " + email);
    }

    // Helper method for hashing passwords (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    // Helper method for verifying hashed passwords
    private boolean verifyPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }
}
