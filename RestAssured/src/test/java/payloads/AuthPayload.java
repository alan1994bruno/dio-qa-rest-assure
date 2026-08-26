package payloads;

public record AuthPayload(
        String username,
        String password
) {}