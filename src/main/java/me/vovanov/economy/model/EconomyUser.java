package me.vovanov.economy.model;

import me.vovanov.economy.repositories.BankAccountRepo;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

public class EconomyUser {
    private int balance;
    private final OfflinePlayer PLAYER;
    private final BankAccountRepo REPO;

    private static final Map<OfflinePlayer, EconomyUser> USER_MAP = new HashMap<>();

    public static EconomyUser getUser(OfflinePlayer player) {
        EconomyUser cachedUser = USER_MAP.get(player);
        if (cachedUser != null) return cachedUser;

        BankAccountRepo data = BankAccountRepo.getRepo(player);
        if (data == null) return null;

        EconomyUser newUser = new EconomyUser(player, data);
        if (!USER_MAP.containsKey(player)) USER_MAP.put(player, newUser);
        return newUser;
    }

    private EconomyUser(OfflinePlayer player, BankAccountRepo repo) {
        this.balance = repo.getBalance();
        this.REPO = repo;
        this.PLAYER = player;
    }

    public int getBalance() {
        return balance;
    }

    public void addToBalance(int toAdd) {
        balance += toAdd;
        REPO.saveBalance(balance);
    }

    public void subtractFromBalance(int toSubtract) {
        balance -= toSubtract;
        REPO.saveBalance(balance);
    }

    public void setBalance(Integer newBalance) {
        balance = newBalance;
        REPO.saveBalance(balance);
    }

    public OfflinePlayer getPlayer() {
        return PLAYER;
    }

}
