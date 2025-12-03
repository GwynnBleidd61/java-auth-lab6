package org.example.auth;

public class App {
    static void main() {
        Database.init();
        String token = AuthService.login("user", "user123");
        System.out.println("Полученный токен: " + token);
        var u = SessionManager.getUserByToken(token);
        System.out.println("Пользователь по токену: " + u.getUsername());
    }
}
