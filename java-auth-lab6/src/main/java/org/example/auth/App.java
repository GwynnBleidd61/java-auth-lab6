package org.example.auth;

public class App {
    public static void main(String[] args) {
        System.out.println("Simple Auth System starting...");

        Database.init();

        // Логиним пользователя
        String userToken = AuthService.login("user", "user123");
        System.out.println("Токен user: " + userToken);

        // Проверяем, что он считается аутентифицированным
        System.out.println("Аутентифицирован до logout: " +
                AuthorizationService.isAuthenticated(userToken));

        // Завершаем сессию
        SessionManager.endSession(userToken);

        // Проверяем ещё раз
        System.out.println("Аутентифицирован после logout: " +
                AuthorizationService.isAuthenticated(userToken));
    }
}
