package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;

import java.io.File;
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
     * Returns the active SQLite database connection.
     * If a connection is closed or doesn't exist - creates a new one.
     *
     * @return The Connection object
     * @throws SQLException in case of an error while connecting
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        try {
            // Explicit driver initialization to isolate the Bukkit class loader
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Economy.getInstance().getLogger().severe("SQLite driver not found!");
            throw new SQLException("SQLite driver not found", e);
        }

        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        return connection;
    }

    /**
     * Closes the current database connection
     */
    public synchronized void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                Economy.getInstance().getLogger().severe("Couldn't close the connection with the database: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
