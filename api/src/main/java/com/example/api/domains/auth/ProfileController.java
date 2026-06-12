package com.example.api.domains.auth;

import com.example.api.domains.auth.domain.Account;
import com.example.api.domains.auth.domain.Profile;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.UpdateProfileRequest;
import com.example.api.domains.auth.responses.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UserRepository directory;

    public ProfileController(UserRepository directory) {
        this.directory = directory;
    }

    @GetMapping("/users/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> show(@PathVariable String id) {
        try {
            User user = Account.locate(id, directory);
            return ResponseEntity.ok(UserResponse.build(user));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(error.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateProfileRequest request) {
        try {
            User user = Profile.revise(id, request, directory);
            return ResponseEntity.ok(UserResponse.build(user));
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(error.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
}
