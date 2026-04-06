package auth.event;

import java.time.Instant;

public record UserLoggedInEvent(
    Long    userId,
    String  username,
    Instant occurredAt
) {}