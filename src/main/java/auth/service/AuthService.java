package auth.service;

import auth.dto.*;
import auth.event.*;
import auth.messaging.EventPublisher;
import auth.model.*;
import auth.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository  userRepo;
    private final RoleRepository  roleRepo;
    private final PasswordEncoder encoder;
    private final EventPublisher  publisher;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo,
                       PasswordEncoder encoder, EventPublisher publisher) {
        this.userRepo  = userRepo;
        this.roleRepo  = roleRepo;
        this.encoder   = encoder;
        this.publisher = publisher;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.username()))
            return AuthResponse.fail("Username already taken");
        if (userRepo.existsByEmail(req.email()))
            return AuthResponse.fail("Email already in use");

        Role role = roleRepo.findByName("USER")
            .orElseGet(() -> roleRepo.save(new Role("USER")));

        User user = new User(req.username(),
                             encoder.encode(req.password()),
                             req.email(), req.phone());
        user.getRoles().add(role);
        User saved = userRepo.save(user);

        publisher.publish(
            "user-" + saved.getId(), "UserRegistered",
            new UserRegisteredEvent(saved.getId(), saved.getUsername(),
                                    saved.getEmail(), Instant.now()));

        return AuthResponse.ok("Registered: " + saved.getUsername(), saved.getId());
    }

    public AuthResponse login(LoginRequest req) {
        return userRepo.findByUsername(req.username())
            .map(user -> {
                if (!encoder.matches(req.password(), user.getPassword()))
                    return AuthResponse.fail("Invalid credentials");

                publisher.publish(
                    "user-" + user.getId(), "UserLoggedIn",
                    new UserLoggedInEvent(user.getId(), user.getUsername(), Instant.now()));

                return AuthResponse.ok("Login successful", user.getId());
            })
            .orElse(AuthResponse.fail("User not found"));
    }
}