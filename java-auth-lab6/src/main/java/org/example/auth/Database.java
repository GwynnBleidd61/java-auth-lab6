package org.example.auth;

import java.sql.*;



public class Database {

    private static final String DB_URL = "jdbc:sqlite:auth.db";

    public static void init() {
        try (Connection connection = getConnection()) {
            System.out.println("Успешное подключение к БД: " + DB_URL);
            createUsersTableIfNotExists(connection);
            insertTestUsersIfEmpty(connection);
            System.out.println("Инициализация БД завершена");
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка инициализации базы данных, e");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не найден драйвер org.sqlite.JDBC. Проверь зависимость в pom.xml", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    private static void createUsersTableIfNotExists(Connection connection) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public static void updateUserPasswordHash(long userId, String newPasswordHash) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);

            int updated = ps.executeUpdate();
            if (updated != 1) {
                throw new RuntimeException("Пароль не обновлён: userId=" + userId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления пароля в БД", e);
        }
    }

    private static void insertTestUsersIfEmpty(Connection connection) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return;
                }
            }
        }

        String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "user");
            ps.setString(2, PasswordHasher.hash("user123"));
            ps.setString(3, "USER");
            ps.executeUpdate();

            ps.setString(1, "admin");
            ps.setString(2, PasswordHasher.hash("admin123"));
            ps.setString(3, "ADMIN");
            ps.executeUpdate();
        }

    }


}
