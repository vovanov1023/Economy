package me.vovanov.economy.model;

import java.util.List;
import java.util.UUID;

public class EconomyUser {
    private int balance;
    private final UUID playerUUID;
    private final List<CreditEntry> activeCredits;

    public EconomyUser(int balance, UUID playerUUID, List<CreditEntry> creditList) {
        this.balance = balance;
        this.playerUUID = playerUUID;
        this.activeCredits = creditList;
    }

    public int getBalance() {
        return this.balance;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
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
