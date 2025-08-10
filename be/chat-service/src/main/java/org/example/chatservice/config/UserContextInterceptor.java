package org.example.chatservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.util.UserContext;
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
        return true;
    }
} 
