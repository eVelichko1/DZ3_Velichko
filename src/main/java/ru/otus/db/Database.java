package ru.otus.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class Database {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Не найден драйвер MySQL. Запускайте программу через Maven.", e);
        }

        Properties props = new Properties();
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("Не найден файл db.properties");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать db.properties", e);
        }

        String host = envOrDefault("DB_HOST", props.getProperty("db.host", "127.0.0.1"));
        String port = envOrDefault("DB_PORT", props.getProperty("db.port", "3306"));
        String name = envOrDefault("DB_NAME", props.getProperty("db.name", "animals_db"));
        USER = envOrDefault("DB_USER", props.getProperty("db.user", "otus"));
        PASSWORD = envOrDefault("DB_PASSWORD", props.getProperty("db.password", "otus"));
        URL = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?createDatabaseIfNotExist=true"
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&characterEncoding=UTF-8"
                + "&serverTimezone=UTC";
    }

    private Database() {
    }

    public static void init() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS animals ("
                            + "id INT NOT NULL AUTO_INCREMENT, "
                            + "type VARCHAR(16) NOT NULL, "
                            + "name VARCHAR(100) NOT NULL, "
                            + "age INT NOT NULL, "
                            + "weight INT NOT NULL, "
                            + "color VARCHAR(16) NOT NULL, "
                            + "PRIMARY KEY (id)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Не удалось подключиться к MySQL или создать таблицу animals: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String envOrDefault(String envName, String fallback) {
        String env = System.getenv(envName);
        if (env == null || env.isBlank()) {
            return fallback;
        }
        return env;
    }
}
