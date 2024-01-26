package com.example.stagefinal.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean authenticated;
    private String message;
    private User user;
    private String token;
    private String[] logMessages;
    private String role; // Include a field for the role
    @Override
    public String toString() {
        return "LoginResponse{" +
                "authenticated=" + authenticated +
                ", user=" + user +
                ", role='" + role + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
