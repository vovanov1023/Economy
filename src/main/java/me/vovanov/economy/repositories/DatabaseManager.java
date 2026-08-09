package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final File dbFile;
    private Connection connection;

    public DatabaseManager(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.dbFile = new File(dataFolder, "economy.db");
    }

    /**
     * Возвращает активное соединение с базой данных SQLite.
     * Если соединение закрыто или отсутствует, открывает новое.
     *
     * @return Connection объект соединения
     * @throws SQLException в случае ошибки подключения
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        try {
            // Явная инициализация драйвера для изоляции класс-лоадера Bukkit
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Economy.getInstance().getLogger().severe("Драйвер SQLite не найден!");
            throw new SQLException("SQLite driver not found", e);
        }

        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        return connection;
    }

    /**
     * Закрывает текущее соединение с базой данных.
     */
    public synchronized void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                Economy.getInstance().getLogger().severe("Ошибка при закрытии соединения с БД: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
