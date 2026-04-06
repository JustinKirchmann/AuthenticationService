package auth.cli;

import auth.dto.*;
import auth.service.AuthService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class AuthShell {

    private final AuthService authService;

    public AuthShell(AuthService authService) {
        this.authService = authService;
    }

    @ShellMethod(key = "register", value = "Register a new user")
    public String register(
        @ShellOption String username,
        @ShellOption String password,
        @ShellOption String email,
        @ShellOption(defaultValue = "") String phone
    ) {
        return authService
            .register(new RegisterRequest(username, password, email, phone))
            .message();
    }

    @ShellMethod(key = "login", value = "Login with credentials")
    public String login(
        @ShellOption String username,
        @ShellOption String password
    ) {
        return authService
            .login(new LoginRequest(username, password))
            .message();
    }
}