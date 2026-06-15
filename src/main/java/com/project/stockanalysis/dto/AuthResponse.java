package com.project.stockanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned on successful authentication containing the JWT and user info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private String username;
    private String email;
    private String role;
    private String message;

    /** Factory method for a successful auth response. */
    public static AuthResponse success(String token, String username, String email, String role) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(username)
                .email(email)
                .role(role)
                .message("Authentication successful")
                .build();
    }

    /** Factory method for an error auth response. */
    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .message(message)
                .build();
    }
}
