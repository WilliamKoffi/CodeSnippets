package com.example.api.domains.auth;

import com.example.api.domains.auth.domain.Registration;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.RegisterRequest;
import com.example.api.domains.auth.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final UserRepository directory;

    public RegistrationController(UserRepository directory) {
        this.directory = directory;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> store(@RequestBody RegisterRequest request) {
        try {
            User user = Registration.process(request, directory);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.build(user));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
}
