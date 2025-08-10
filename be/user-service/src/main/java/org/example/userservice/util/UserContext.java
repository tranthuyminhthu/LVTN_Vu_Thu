package org.example.userservice.util;

import lombok.extern.slf4j.Slf4j;
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
            // Try to get from request attribute first (set by JWT filter)
            String userId = (String) request.getAttribute("X-User-ID");
            if (userId != null) {
                return userId;
            }
            // Fallback to header
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
            // Try to get from request attribute first (set by JWT filter)
            String rolesAttr = (String) request.getAttribute("X-User-Roles");
            if (rolesAttr != null && !rolesAttr.isEmpty()) {
                return Arrays.asList(rolesAttr.split(","));
            }

            // Fallback: try to get from custom header (for internal calls)
            String rolesHeader = request.getHeader(USER_ROLES_HEADER);
            if (rolesHeader != null && !rolesHeader.isEmpty()) {
                return Arrays.asList(rolesHeader.split(","));
            }
        }

        return List.of("USER");
    }

    public static boolean hasUserInfo() {
        return getCurrentUserId() != null;
    }

    public static boolean hasAnyRole(String... requiredRoles) {
        List<String> userRoles = getCurrentUserRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        
        for (String role : requiredRoles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAllRoles(String... requiredRoles) {
        List<String> userRoles = getCurrentUserRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        
        for (String role : requiredRoles) {
            if (!userRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getRoles() {
        return getCurrentUserRoles();
    }

    public static String getUserId() {
        return getCurrentUserId();
    }

    public static void logCurrentUserRoles() {
        String userId = getCurrentUserId();
        String email = getCurrentUserEmail();
        List<String> roles = getCurrentUserRoles();
        
        if (userId != null) {
            log.info("User Service - User {} (email: {}) has roles: {}", 
                    userId, 
                    email != null ? email : "N/A", 
                    roles);
        } else {
            log.info("User Service - Anonymous request - no user context");
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
