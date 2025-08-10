package org.example.cartservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class UserContext {
    public static final String USER_ID_HEADER = "X-User-ID";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_NAME_HEADER = "X-User-Name";
    public static final String USER_ROLES_HEADER = "X-User-Roles";

    public static String getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(USER_ID_HEADER);
        }
        return null;
    }

    public static String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(USER_EMAIL_HEADER);
        }
        return null;
    }

    public static String getCurrentUserName() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return request.getHeader(USER_NAME_HEADER);
        }
        return null;
    }

    public static List<String> getCurrentUserRoles() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String rolesHeader = request.getHeader(USER_ROLES_HEADER);
            if (rolesHeader != null && !rolesHeader.isEmpty()) {
                return Arrays.asList(rolesHeader.split(","));
            }
        }
        return List.of("USER"); // Default role
    }

    public static boolean hasUserInfo() {
        return getCurrentUserId() != null;
    }

    public static void logCurrentUserRoles() {
        String userId = getCurrentUserId();
        String email = getCurrentUserEmail();
        List<String> roles = getCurrentUserRoles();
        
        if (userId != null) {
            log.info("Cart Service - User {} (email: {}) has roles: {}", 
                    userId, 
                    email != null ? email : "N/A", 
                    roles);
        } else {
            log.info("Cart Service - Anonymous request - no user context");
        }
    }

    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
} 