package com.tracker.penalty.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank public String username;
        @NotBlank public String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank public String username;
        @NotBlank public String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RefreshRequest {
        @NotBlank public String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class AuthResponse {
        public String token;
        public String refreshToken;

        public AuthResponse(String token, String refreshToken) {
            this.token = token;
            this.refreshToken = refreshToken;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}
