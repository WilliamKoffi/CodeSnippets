package com.example.api.domains.auth;

import com.example.api.domains.auth.domain.Session;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.LoginRequest;
import com.example.api.domains.auth.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class SessionController {

    private final UserRepository directory;

    public SessionController(UserRepository directory) {
        this.directory = directory;
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    public ResponseEntity<?> store(@RequestBody LoginRequest request) {
        try {
            User user = Session.establish(request, directory);
            return ResponseEntity.ok(UserResponse.build(user));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(error.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
}
