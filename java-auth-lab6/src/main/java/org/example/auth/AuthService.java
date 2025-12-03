package org.example.auth;

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
