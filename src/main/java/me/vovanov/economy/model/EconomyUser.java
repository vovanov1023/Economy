package me.vovanov.economy.model;

import me.vovanov.economy.repositories.BankAccountRepo;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class EconomyUser {
    private int balance;
    private final List<Credit> CREDIT_LIST;
    private final OfflinePlayer PLAYER;
    private final BankAccountRepo DATA;

    public static EconomyUser getUser(OfflinePlayer player) {
        BankAccountRepo data = BankAccountRepo.getData(player);
        if (data == null) return null;
        return new EconomyUser(player, data);
    }

    private EconomyUser(OfflinePlayer player, BankAccountRepo data) {
        this.balance = data.getBalance();
        this.DATA = data;
        this.PLAYER = player;
        this.CREDIT_LIST = data.getCreditIDList().stream().map(Credit::getCredit).toList();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(Integer newBalance) {
        balance = newBalance;
        DATA.saveBalance(newBalance);
    }

    public OfflinePlayer getPlayer() {
        return PLAYER;
    }

    public List<Credit> getCreditList() {
        return CREDIT_LIST;
    }
}
