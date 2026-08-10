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
    private static final ConcurrentHashMap<UUID, EconomyUser> economyUserMap = new ConcurrentHashMap<>();
    private static BankAccountRepository repository;

    public static void setup(BankAccountRepository repo) {
        repository = repo;
    }

    public static EconomyUser getUser(OfflinePlayer player) {
        return getUser(player.getUniqueId());
    }

    public static EconomyUser getUser(UUID uuid) {
        return economyUserMap.computeIfAbsent(uuid, k -> repository.load(uuid));
    }

    public static EconomyUser createBankAccount(UUID playerID) {
        repository.createNewAccount(playerID);
        return getUser(playerID);
    }

    public static EconomyUser createBankAccount(OfflinePlayer player) {
        return createBankAccount(player.getUniqueId());
    }

    public static void unloadUser(OfflinePlayer player) {
        EconomyUser user = economyUserMap.remove(player.getUniqueId());
        if (user != null) Bukkit.getScheduler().runTaskAsynchronously(Economy.getInstance(), task -> repository.save(user));
    }

    public static void saveUsers() {
        for (EconomyUser user : economyUserMap.values()) {
            repository.save(user);
        }
    }

    public static void modifyBalance(EconomyUser user, int amount) {
        user.deposit(amount);
    }

    public static void setBalance(EconomyUser user, int newBalance) {
        user.setBalance(newBalance);
    }

    public static TransactionResult transferMoney(EconomyUser sender, EconomyUser receiver, int amount, String comment) {

        if (sender == null) return TransactionResult.SENDER_NOT_FOUND;
        if (receiver == null) return TransactionResult.RECEIVER_NOT_FOUND;

        int senderBalance = sender.getBalance();
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        if (senderBalance < amount) return TransactionResult.INSUFFICIENT_FUNDS;

        sender.withdraw(amount);
        receiver.deposit(amount);
        repository.save(sender);
        repository.save(receiver);

        String finalComment = (comment == null || comment.isEmpty()) ? "No comment" : comment;
        String senderName = org.bukkit.Bukkit.getOfflinePlayer(sender.getPlayerUUID()).getName();
        String receiverName = org.bukkit.Bukkit.getOfflinePlayer(receiver.getPlayerUUID()).getName();
        DiscordIntegrationService.getInstance().sendToLogChannel(senderName+" transferred "+amount+" "+ConfigService.getInstance().getCurrencyAlias()+" to "+receiverName+"\nComment: "+finalComment);

        return TransactionResult.SUCCESS;
    }

}
