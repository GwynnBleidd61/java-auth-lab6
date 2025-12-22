package org.example.auth;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthService {

    public static String login(String username, String password) {
        User user = findUserByUsername(username);

        if (user == null) {
            System.out.println("Пользователь не найден");
            return null;
        }

        boolean passwordMatch = PasswordHasher.verify(password, user.getPasswordHash());
        if (!passwordMatch) {
            System.out.println("Неверный пароль");
            return null;
        }

        String token = UUID.randomUUID().toString();

        SessionManager.createSession(user, token);

        System.out.println("Аунтентификация успешна. Токен: " + token);
        return token;
    }

    public static boolean changePassword(String token, String oldPassword, String newPassword) {
        User user = SessionManager.getUserByToken(token);
        if (user == null) {
            System.out.println("Сессия недействительна. Выполните вход заново.");
            return false;
        }

        if (!PasswordHasher.verify(oldPassword, user.getPasswordHash())) {
            System.out.println("Старый пароль неверен.");
            return false;
        }

        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("Новый пароль слишком короткий (минимум 6 символов)");
            return false;
        }

        String newHash = PasswordHasher.hash(newPassword);
        Database.updateUserPasswordHash(user.getId(), newHash);

        SessionManager.endSession(token);

        System.out.println("Пароль успешно изменен. Пожалуйста войдите снова.");
        return true;
    }


    public static boolean changeUserRole(String adminToken, String targetUsername, String newRole) {
        User admin = SessionManager.getUserByToken(adminToken);
        if (admin == null) {
            System.out.println("Сессия недействительна. Войдите снова");
            return false;
        }

        if (!AuthorizationService.canAccess(adminToken, "MANAGE_USERS")) {
            System.out.println("Доступ зарпрещён. Нужны права администратора (MANAGE_USERS).");
            return false;
        }

        String roleUpper = newRole == null ? "" : newRole.trim().toUpperCase();
        if(!roleUpper.equals("USER") && !roleUpper.equals("ADMIN")) {
            System.out.println("Недопустимая роль. Разрешены: USER, ADMIN");
            return false;
        }

        User target = Database.findUserByUsername(targetUsername);
        if (target == null) {
            System.out.println("Пользователь не найден: " + targetUsername);
            return false;
        }

        Database.updateUserRole(target.getId(), roleUpper);

        System.out.println("Роль пользователя " + targetUsername + " изменена на " + roleUpper);

        return true;
    }


    private static User findUserByUsername(String username) {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя, e");
        }

        return null;
    }

}
