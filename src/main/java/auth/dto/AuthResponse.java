package auth.dto;

public record AuthResponse(boolean success, String message, Long userId) {
    public static AuthResponse ok(String message, Long userId) {
        return new AuthResponse(true, message, userId);
    }
    public static AuthResponse fail(String message) {
        return new AuthResponse(false, message, null);
    }
}