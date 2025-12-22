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
        boolean running = true;

        while (running) {
            printMenu();
            System.out.println("Выберите пункт меню: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin(scanner);
                case "2" -> handlePublicResource();
                case "3" -> handleUserResource();
                case "4" -> handleAdminResource();
                case "5" -> handleLogout();
                case "6" -> handleChangePassword(scanner);
                case "7" -> handleRoleManagement(scanner);
                case "0" -> {
                    running = false;
                    System.out.println("Выход из программы. До свидания!");
                }
                default -> System.out.println("Неизвестный пункт меню. Попробуйте ещё раз.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("---------------");
        System.out.println("Текущий пользователь: " + getCurrentUsernameOrGuest());
        System.out.println("1. Войти (логин)");
        System.out.println("2. Публичный ресурс (доступен всем)");
        System.out.println("3. Пользовательский ресурс (требует роли USER)");
        System.out.println("4. Админский ресурс (требует роли ADMIN)");
        System.out.println("5. Выйти из аккаунта (logout)");
        System.out.println("6. Сменить пароль (требует вход)");
        System.out.println("7. Управление ролями (только ADMIN)");
        System.out.println("0. Выход");
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

    private static void handlePublicResource() {
        System.out.println("[Публичный ресурс]");
        System.out.println("Эта информация доступна всем, даже без входа в систему.");
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

    private static void handleAdminResource() {
        if (currentToken == null || !AuthorizationService.isAuthenticated(currentToken)) {
            System.out.println("Вы не залогинены. Пожалуйста, войдите в систему (пункт 1).");
            return;
        }

        boolean allowed = AuthorizationService.canAccess(currentToken, "VIEW_ADMIN_PANEL");
        if (!allowed) {
            System.out.println("Доступ к админскому ресурсу запрещён. Нужна роль ADMIN.");
            return;
        }

        User user = SessionManager.getUserByToken(currentToken);
        System.out.println("[Админский ресурс]");
        System.out.println("Здравствуйте, Администратор " + user.getUsername() + "!");
        System.out.println("Здесь может быть управление пользователями, логами и т.д.");
    }

    private static void handleRoleManagement(Scanner scanner) {
        if (currentToken == null || !AuthorizationService.isAuthenticated(currentToken)) {
            System.out.println("Вы не залогинены. Сначала выполните вход (пункт 1).");
            return;
        }

        if (!AuthorizationService.canAccess(currentToken, "MANAGE_USERS")) {
            System.out.println("Доступ запрещён. Нужна роль ADMIN.");
            return;
        }

        System.out.print("Введите username пользователя, которому меняем роль: ");
        String targetUsername = scanner.nextLine().trim();

        System.out.print("Введите новую роль (USER/ADMIN): ");
        String newRole = scanner.nextLine().trim();

        AuthService.changeUserRole(currentToken, targetUsername, newRole);
    }

    private static void handleChangePassword(Scanner scanner) {
        if (currentToken == null || !AuthorizationService.isAuthenticated(currentToken)) {
            System.out.println("Вы не залогинены. Сначала выполните вход (пункт 1).");
            return;
        }

        System.out.println("Введите старый пароль: ");
        String oldPass = scanner.nextLine().trim();

        System.out.println("Введите новый пароль: ");
        String newPass = scanner.nextLine().trim();

        boolean ok = AuthService.changePassword(currentToken, oldPass, newPass);
        if (ok) {
            currentToken = null;
        }
    }

    private static void handleLogout() {
        if (currentToken == null) {
            System.out.println("Вы не залогинены.");
            return;
        }

        SessionManager.endSession(currentToken);
        currentToken = null;
        System.out.println("Вы вышли из системы");
    }
}

