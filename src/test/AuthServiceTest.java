package auth.service;

import auth.dto.*;
import auth.messaging.EventPublisher;
import auth.model.*;
import auth.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository  userRepo;
    @Mock RoleRepository  roleRepo;
    @Mock PasswordEncoder encoder;
    @Mock EventPublisher  publisher;

    @InjectMocks AuthService authService;

    private final RegisterRequest REG =
        new RegisterRequest("alice", "pass", "alice@example.com", "+43123");

    @Test
    void register_success() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        when(userRepo.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepo.findByName("USER")).thenReturn(Optional.of(new Role("USER")));
        when(encoder.encode("pass")).thenReturn("$2a$hashed");
        when(userRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse res = authService.register(REG);

        assertTrue(res.success());
        assertTrue(res.message().contains("alice"));
        verify(publisher, times(1)).publish(any(), eq("UserRegistered"), any());
    }

    @Test
    void register_duplicateUsername() {
        when(userRepo.existsByUsername("alice")).thenReturn(true);

        AuthResponse res = authService.register(REG);

        assertFalse(res.success());
        assertEquals("Username already taken", res.message());
        verifyNoInteractions(publisher);
    }

    @Test
    void register_duplicateEmail() {
        when(userRepo.existsByUsername("alice")).thenReturn(false);
        when(userRepo.existsByEmail("alice@example.com")).thenReturn(true);

        AuthResponse res = authService.register(REG);

        assertFalse(res.success());
        assertEquals("Email already in use", res.message());
    }

    @Test
    void login_success() {
        User user = new User("alice", "$2a$hashed", "alice@example.com", "");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(encoder.matches("pass", "$2a$hashed")).thenReturn(true);

        AuthResponse res = authService.login(new LoginRequest("alice", "pass"));

        assertTrue(res.success());
        assertEquals("Login successful", res.message());
        verify(publisher, times(1)).publish(any(), eq("UserLoggedIn"), any());
    }

    @Test
    void login_wrongPassword() {
        User user = new User("alice", "$2a$hashed", "alice@example.com", "");
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "$2a$hashed")).thenReturn(false);

        AuthResponse res = authService.login(new LoginRequest("alice", "wrong"));

        assertFalse(res.success());
        assertEquals("Invalid credentials", res.message());
        verifyNoInteractions(publisher);
    }

    @Test
    void login_userNotFound() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        AuthResponse res = authService.login(new LoginRequest("ghost", "x"));

        assertFalse(res.success());
        assertEquals("User not found", res.message());
    }
}