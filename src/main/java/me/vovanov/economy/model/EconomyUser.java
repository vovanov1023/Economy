package me.vovanov.economy.model;

import java.util.UUID;

public class EconomyUser {
    private int balance;
    private final UUID PLAYER_UUID;

    public EconomyUser(int balance, UUID playerUUID) {
        this.balance = balance;
        this.PLAYER_UUID = playerUUID;
    }

    public int getBalance() {
        return balance;
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

    public UUID getPlayerUUID() {
        return PLAYER_UUID;
    }

}
