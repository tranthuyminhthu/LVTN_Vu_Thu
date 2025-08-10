package org.example.productsservice.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;
import java.util.List;

@Component
@RequestScope
public class UserContext {
    private String userId;
    private String email;
    private String username;
    private List<String> roles;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setRolesFromHeader(String rolesHeader) {
        if (rolesHeader != null && !rolesHeader.isEmpty()) {
            this.roles = Arrays.asList(rolesHeader.split(","));
        } else {
            this.roles = List.of("USER"); // Default role
        }
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        if (this.roles == null) return false;
        return Arrays.stream(roles).anyMatch(this.roles::contains);
    }

    public boolean hasAllRoles(String... roles) {
        if (this.roles == null) return false;
        return Arrays.stream(roles).allMatch(this.roles::contains);
    }
} 