package com.example.api.domains.auth;

import com.example.api.domains.auth.domain.Account;
import com.example.api.domains.auth.domain.Password;
import com.example.api.domains.auth.domain.Profile;
import com.example.api.domains.auth.domain.Recovery;
import com.example.api.domains.auth.domain.Registration;
import com.example.api.domains.auth.domain.Session;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.LoginRequest;
import com.example.api.domains.auth.requests.RegisterRequest;
import com.example.api.domains.auth.requests.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthSlicesTests {

    private UserRepository directory;
    private JavaMailSender postbox;

    @BeforeEach
    void setUp() {
        directory = mock(UserRepository.class);
        postbox = mock(JavaMailSender.class);
    }

    @Test
    void registrationCleansHandleBeforeCheckAndSave() {
        RegisterRequest request = new RegisterRequest("Ada", "@ada", "ada@example.com", "secret", null);

        when(directory.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = Registration.process(request, directory);

        assertEquals("ada", user.handle());
        assertEquals("fullstack", user.role());
        assertEquals("Aspirant", user.level());
        assertEquals(120, user.reputation());
        assertTrue(Password.matches("secret", user.password()));
        verify(directory).existsByHandle("ada");
    }

    @Test
    void sessionRejectsWrongPassword() {
        User user = new User("user-1", "Ada", "ada", "ada@example.com", Password.secure("secret"), "avatar", 120, "fullstack", "Aspirant");
        when(directory.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Session.establish(new LoginRequest("ada@example.com", "wrong"), directory));

        assertEquals("Invalid email or password", error.getMessage());
    }

    @Test
    void profileRevisesCleanHandleAndRole() {
        User user = new User("user-1", "Ada", "ada", "ada@example.com", "hash", "avatar", 120, "fullstack", "Aspirant");
        UpdateProfileRequest request = new UpdateProfileRequest("Grace", "@grace", "portrait", "backend", "Elite");

        when(directory.findById("user-1")).thenReturn(Optional.of(user));
        when(directory.save(user)).thenReturn(user);

        User revised = Profile.revise("user-1", request, directory);

        assertSame(user, revised);
        assertEquals("Grace", user.name());
        assertEquals("grace", user.handle());
        assertEquals("portrait", user.avatar());
        assertEquals("backend", user.role());
        assertEquals("Elite", user.level());
        verify(directory).existsByHandle("grace");
    }

    @Test
    void recoveryDispatchesResetMail() {
        User user = new User("user-1", "Ada", "ada", "ada@example.com", "hash", "avatar", 120, "fullstack", "Aspirant");
        when(directory.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        Recovery.initiate("ada@example.com", directory, postbox);

        verify(postbox).send(any(SimpleMailMessage.class));
    }

    @Test
    void accountLocatesExistingUser() {
        User user = new User("user-1", "Ada", "ada", "ada@example.com", "hash", "avatar", 120, "fullstack", "Aspirant");
        when(directory.findById("user-1")).thenReturn(Optional.of(user));

        User located = Account.locate("user-1", directory);

        assertSame(user, located);
    }
}
