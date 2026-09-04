package me.vovanov.economy.services;

import me.vovanov.economy.Economy;
import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.repositories.BankAccountRepository;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyUserService {
    private final ConcurrentHashMap<UUID, EconomyUser> economyUserMap = new ConcurrentHashMap<>();
    private final BankAccountRepository repository;

    public EconomyUserService(BankAccountRepository repo) {
        this.repository = repo;
    }

    public EconomyUser getUser(OfflinePlayer player) {
        return getUser(player.getUniqueId());
    }

    public EconomyUser getUser(UUID uuid) {
        return economyUserMap.computeIfAbsent(uuid, k -> repository.load(uuid));
    }

    public EconomyUser createBankAccount(UUID playerID) {
        repository.createNewAccount(playerID);
        return getUser(playerID);
    }

    public EconomyUser createBankAccount(OfflinePlayer player) {
        return createBankAccount(player.getUniqueId());
    }

    public void unloadUser(OfflinePlayer player) {
        EconomyUser user = economyUserMap.remove(player.getUniqueId());
        if (user != null) Bukkit.getScheduler().runTaskAsynchronously(Economy.getInstance(), task -> repository.save(user));
    }

    public void saveUsers() {
        for (EconomyUser user : economyUserMap.values()) {
            repository.save(user);
        }
    }

    public void modifyBalance(EconomyUser user, int amount) {
        user.deposit(amount);
    }

    public void setBalance(EconomyUser user, int newBalance) {
        user.setBalance(newBalance);
    }

}
