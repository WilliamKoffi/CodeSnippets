package com.example.api.domains.auth;

import com.example.api.domains.auth.domain.Recovery;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.ResetPasswordRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class RecoveryController {

    private final UserRepository directory;
    private final JavaMailSender postbox;

    public RecoveryController(UserRepository directory, JavaMailSender postbox) {
        this.directory = directory;
        this.postbox = postbox;
    }

    @PostMapping("/reset")
    @Transactional(readOnly = true)
    public ResponseEntity<?> store(@RequestBody ResetPasswordRequest request) {
        try {
            Recovery.initiate(request.email(), directory, postbox);
            return ResponseEntity.ok(new MessageResponse("Simulated recovery link sent"));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
    public record MessageResponse(String message) {}
}
