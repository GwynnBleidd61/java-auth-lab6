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

        String insertSql = "INSERT INTO users (username, password, role) VALUE (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, "user");
            ps.setString(2, "user123");
            ps.setString(3, "USER");
            ps.executeUpdate();

            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.setString(3, "ADMIN");
            ps.executeUpdate();
        }

    }


}
