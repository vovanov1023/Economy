package me.vovanov.economy.repositories;

import me.vovanov.economy.Economy;
import me.vovanov.economy.model.CreditEntry;
import me.vovanov.economy.model.EconomyUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteBankAccountRepository implements BankAccountRepository {
    private final DatabaseManager dbManager;

    public SQLiteBankAccountRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void setup() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Bank accounts (players) table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "balance INT DEFAULT 0" +
                    ");");

            // Player credits table
            stmt.execute("CREATE TABLE IF NOT EXISTS credits (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "player_uuid VARCHAR(36) NOT NULL, " +
                    "amount INT NOT NULL, " +
                    "original_principal INT NOT NULL, " +
                    "interest_rate DOUBLE NOT NULL, " +
                    "deadline BIGINT NOT NULL" +
                    ");");
            // ATMs table
            stmt.execute("CREATE TABLE IF NOT EXISTS atms (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    world TEXT NOT NULL," +
                    "    x INTEGER NOT NULL," +
                    "    y INTEGER NOT NULL," +
                    "    z INTEGER NOT NULL," +
                    "    UNIQUE(world, x, y, z)" +
                    ");");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_credits_player ON credits(player_uuid);");
        }
    }

    @Override
    public void createNewAccount(UUID playerUUID) {
        String query = "INSERT OR IGNORE INTO accounts (uuid, balance) VALUES (?, 0);";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, playerUUID.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            Economy.getInstance().getLogger().severe("Couldn't create an account for " + playerUUID + ": " + e.getMessage());
        }
    }

    @Override
    public EconomyUser load(UUID uuid) {
        String uuidStr = uuid.toString();
        
        String balanceQuery = "SELECT balance FROM accounts WHERE uuid = ?;";
        String creditsQuery = "SELECT id, amount, original_principal, interest_rate, deadline FROM credits WHERE player_uuid = ?;";

        try (Connection conn = dbManager.getConnection()) {
            
            // 1. Loading the balance
            int balance = 0;
            boolean accountExists = false;
            try (PreparedStatement psBalance = conn.prepareStatement(balanceQuery)) {
                psBalance.setString(1, uuidStr);
                try (ResultSet rs = psBalance.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getInt("balance");
                        accountExists = true;
                    }
                }
            }

            // Return null if the account doesn't exist
            if (!accountExists) {
                return null;
            }

            // 2. Load the credits
            List<CreditEntry> credits = new ArrayList<>();
            try (PreparedStatement psCredits = conn.prepareStatement(creditsQuery)) {
                psCredits.setString(1, uuidStr);
                try (ResultSet rs = psCredits.executeQuery()) {
                    while (rs.next()) {
                        UUID creditId = UUID.fromString(rs.getString("id"));
                        int amount = rs.getInt("amount");
                        int original = rs.getInt("original_principal");
                        double rate = rs.getDouble("interest_rate");
                        long deadline = rs.getLong("deadline");

                        credits.add(new CreditEntry(creditId, original, amount, rate, deadline));
                    }
                }
            }

            return new EconomyUser(balance, uuid, credits);

        } catch (SQLException e) {
            Economy.getInstance().getLogger().severe("Couldn't load data for " + uuidStr + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(EconomyUser user) {
        synchronized(dbManager) {
            UUID uuid = user.getPlayerUUID();
            String uuidStr = uuid.toString();

            String saveBalanceQuery = "INSERT OR REPLACE INTO accounts (uuid, balance) VALUES (?, ?);";
            String deleteCreditsQuery = "DELETE FROM credits WHERE player_uuid = ?;";
            String insertCreditQuery = "INSERT INTO credits (id, player_uuid, amount, original_principal, interest_rate, deadline) VALUES (?, ?, ?, ?, ?, ?);";

            Connection conn = null;
            try {
                conn = dbManager.getConnection();

                conn.setAutoCommit(false);

                // 1. Saving the balance
                try (PreparedStatement psBalance = conn.prepareStatement(saveBalanceQuery)) {
                    psBalance.setString(1, uuidStr);
                    psBalance.setInt(2, user.getBalance());
                    psBalance.executeUpdate();
                }

                // 2. Clearing the old credit data
                try (PreparedStatement psDelete = conn.prepareStatement(deleteCreditsQuery)) {
                    psDelete.setString(1, uuidStr);
                    psDelete.executeUpdate();
                }

                // 3. Pasting the current credit data
                if (!user.getActiveCredits().isEmpty()) {
                    try (PreparedStatement psInsert = conn.prepareStatement(insertCreditQuery)) {
                        for (CreditEntry credit : user.getActiveCredits()) {
                            psInsert.setString(1, credit.getId().toString());
                            psInsert.setString(2, uuidStr);
                            psInsert.setInt(3, credit.getAmount());
                            psInsert.setInt(4, credit.getOriginalPrincipal());
                            psInsert.setDouble(5, credit.getInterestRate());
                            psInsert.setLong(6, credit.getDeadline());

                            psInsert.addBatch();
                        }
                        psInsert.executeBatch();
                    }
                }

                // Confirm
                conn.commit();

            } catch (SQLException e) {
                Economy.getInstance().getLogger().severe("Couldn't save data for " + uuidStr + ": " + e.getMessage());
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback the transaction in case of an error
                    } catch (SQLException rollbackEx) {
                        Economy.getInstance().getLogger().severe("Couldn't roll back the transaction: " + rollbackEx.getMessage());
                    }
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Enabling autoCommit back
                    } catch (SQLException e) {
                        Economy.getInstance().getLogger().severe("Couldn't enable autoCommit back: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        dbManager.close();
    }
}
