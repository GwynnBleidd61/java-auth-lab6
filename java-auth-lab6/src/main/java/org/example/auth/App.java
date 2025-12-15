package org.example.auth;

import java.util.Scanner;

public class App {

    private static String currentToken = null;

    public static void main(String[] args) {
        System.out.println("=== Simple Auth System ===");

        Database.init();

        runConsole();
    }

    private static void runConsole() {
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1"
        }
    }

    private static String getCurrentUsernameOrGuest() {
        if (currentToken == null) {
            return "гость";
        }
        User user = SessionManager.getUserByToken(currentToken);
        if (user == null) {
            return "гость";
        }
        return user.getUsername() + " (" + user.getRole() + ")";
    }

    private static void handleLogin(Scanner scanner) {
        if (currentToken != null && AuthorizationService.isAuthenticated(currentToken)) {
            System.out.println("Вы уже залогинены как: " + getCurrentUsernameOrGuest());
            System.out.println("Если вы хотите зайти под другим пользователем - сначала выйдите (пункт 5).");
            return;
        }

        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine().trim();

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        String token = AuthService.login(username, password);
        if (token == null) {
            System.out.println("Не удалось войти. Проверьте логин/пароль.");
        } else {
            currentToken = token;
            System.out.println("Вход выполнен успешно. Токен: " + token);
        }
    }

    private static void handleUserResource() {
        if (currentToken == null || !AuthorizationService.isAuthenticated(currentToken)) {
            System.out.println("Вы не залогинены. Пожалуйста войдите в систему (пункт 1).");
            return;
        }

        boolean allowed = AuthorizationService.canAccess(currentToken, "VIEW_DASHBOARD");
        if (!allowed) {
            System.out.println("Доступ к пользовательскому ресурсу запрещён. Недостаточно прав");
            return;
        }

        User user = SessionManager.getUserByToken(currentToken);
        System.out.println("[Пользовательский ресурс]");
        System.out.println("Здравствуйте, " + user.getUsername() + "!");
        System.out.println("Это ваша пользовательская панель (доступна ролям USER и ADMIN).");
    }

    private

}

