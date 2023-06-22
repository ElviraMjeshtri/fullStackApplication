package com.elacode.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
