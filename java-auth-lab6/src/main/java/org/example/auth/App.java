package org.example.auth;

public class App {
    public static void main(String[] args) {
        System.out.println("Simple Auth System starting...");

        Database.init();

        // Логиним обычного пользователя
        String userToken = AuthService.login("user", "user123");
        System.out.println("Токен user: " + userToken);

        // Логиним админа
        String adminToken = AuthService.login("admin", "admin123");
        System.out.println("Токен admin: " + adminToken);

        System.out.println("\n--- Проверка прав доступа ---");

        // USER
        System.out.println("USER can VIEW_DASHBOARD: " +
                AuthorizationService.canAccess(userToken, "VIEW_DASHBOARD"));
        System.out.println("USER can VIEW_ADMIN_PANEL: " +
                AuthorizationService.canAccess(userToken, "VIEW_ADMIN_PANEL"));

        // ADMIN
        System.out.println("ADMIN can VIEW_DASHBOARD: " +
                AuthorizationService.canAccess(adminToken, "VIEW_DASHBOARD"));
        System.out.println("ADMIN can VIEW_ADMIN_PANEL: " +
                AuthorizationService.canAccess(adminToken, "VIEW_ADMIN_PANEL"));
        System.out.println("ADMIN can MANAGE_USERS: " +
                AuthorizationService.canAccess(adminToken, "MANAGE_USERS"));
    }
}
