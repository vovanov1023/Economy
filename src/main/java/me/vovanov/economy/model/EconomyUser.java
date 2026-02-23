package me.vovanov.economy.model;

import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public class EconomyUser {
    private int balance;
    private final UUID PLAYER_UUID;
    private final OfflinePlayer PLAYER;
    private List<CreditEntry> activeCredits;

    public EconomyUser(int balance, OfflinePlayer player) {
        this.balance = balance;
        this.PLAYER_UUID = player.getUniqueId();
        this.PLAYER = player;
    }

    public int getBalance() {
        return this.balance;
    }

    public UUID getPlayerUUID() {
        return this.PLAYER_UUID;
    }

    public OfflinePlayer getPlayer() {
        return this.PLAYER;
    }

    public List<CreditEntry> getActiveCredits() {
        return this.activeCredits;
    }

    public void deposit(int toAdd) {
        balance += toAdd;
    }

    public void withdraw(int toSubtract) {
        balance -= toSubtract;
    }

    public void setBalance(int newBalance) {
        balance = newBalance;
    }

}
