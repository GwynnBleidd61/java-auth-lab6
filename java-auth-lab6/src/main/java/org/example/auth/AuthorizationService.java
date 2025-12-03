package org.example.auth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthorizationService {

    private static final Map<String, Set<String>> ROLE_PERMISSIONS = new HashMap<>();

    static {
        Set<String> userPermissions = new HashSet<>();
        userPermissions.add("VIEW_DASHBOARD");
        userPermissions.add("VIEW_PROFILE");

        Set<String> adminPermissions = new HashSet<>(userPermissions);
        adminPermissions.add("VIEW_ADMIN_PANEL");
        adminPermissions.add("MANAGE_USERS");

        ROLE_PERMISSIONS.put("USER", userPermissions);
        ROLE_PERMISSIONS.put("ADMIN", adminPermissions);
    }

    public static boolean isAuthenticated(String token) {
        return SessionManager.getUserByToken(token) != null;
    }

    public static boolean hasRole(String token, String requiredRole) {
        User user = SessionManager.getUserByToken(token);
        if (user == null) {
            return false;
        }
        String userRole = user.getRole();
        if (userRole == null) {
            return false;
        }

        return userRole.equalsIgnoreCase(requiredRole);
    }

    public static boolean canAccess(String token, String permission) {
        User user = SessionManager.getUserByToken(token);
        if (user == null) {
            return false;
        }

        String role = user.getRole();
        if (role == null) {
            return false;
        }

        Set<String> permissions = ROLE_PERMISSIONS.get(role.toUpperCase());
        if (permissions == null) {
            return false;
        }

        return permissions.contains(permission);
    }

    public static void checkAccessOrThrow(String token, String permission) {
        if (!canAccess(token, permission)) {
            throw new SecurityException("Доступ запрещён: недостаточно прав для операции" + permission);
        }
    }
}







