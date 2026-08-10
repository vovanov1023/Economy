package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;
import me.vovanov.economy.model.ATM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteATMRepository implements ATMRepository {

    private final DatabaseManager dbManager;

    public SQLiteATMRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void setup() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS atms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "world TEXT NOT NULL," +
                "x INTEGER NOT NULL," +
                "y INTEGER NOT NULL," +
                "z INTEGER NOT NULL," +
                "UNIQUE(world, x, y, z)" +
                ");";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public List<ATM> loadAllATMs() {
        List<ATM> atms = new ArrayList<>();
        String sql = "SELECT * FROM atms";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                atms.add(new ATM(
                        rs.getInt("id"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z"),
                        rs.getString("world")
                ));
            }
        } catch (Exception e) {
            Economy.getInstance().getLogger().severe("Failed to load the ATMs: " + e.getMessage());
        }
        return atms;
    }

    @Override
    public ATM createATM(String worldName, int x, int y, int z) {
        String sql = "INSERT INTO atms(world, x, y, z) VALUES(?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, worldName);
            pstmt.setInt(2, x);
            pstmt.setInt(3, y);
            pstmt.setInt(4, z);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new ATM(id, x, y, z, worldName);
                }
            }
        } catch (Exception e) {
            Economy.getInstance().getLogger().warning("Couldn't create an ATM (maybe it already exists?): " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteATM(int id) {
        String sql = "DELETE FROM atms WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            Economy.getInstance().getLogger().severe("Failed to delete an ATM: " + e.getMessage());
        }
    }
}
