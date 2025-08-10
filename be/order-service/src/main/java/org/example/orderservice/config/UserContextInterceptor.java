package org.example.orderservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    private final UserContext userContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Set user information from headers
        String userId = request.getHeader("X-User-ID");
        String email = request.getHeader("X-User-Email");
        String username = request.getHeader("X-User-Name");
        String rolesHeader = request.getHeader("X-User-Roles");

        userContext.setUserId(userId);
        userContext.setEmail(email);
        userContext.setUsername(username);
        userContext.setRolesFromHeader(rolesHeader);

        // Log current user roles
        if (userId != null) {
            log.info("Order Service - User {} (email: {}) has roles: {}",
                    userId,
                    email != null ? email : "N/A",
                    userContext.getRoles() != null ? userContext.getRoles() : "No roles");
        } else {
            log.info("Order Service - Anonymous request - no user context");
        }

        return true;
    }
} 