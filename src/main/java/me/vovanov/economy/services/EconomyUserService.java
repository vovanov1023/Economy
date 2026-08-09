package me.vovanov.economy.services;

import me.vovanov.economy.Economy;
import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.repositories.BankAccountRepository;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyUserService {
    private static final Map<UUID, EconomyUser> economyUserMap = new HashMap<>();

    public static EconomyUser getUser(OfflinePlayer player) {
        return getUser(player.getUniqueId());
    }

    public static EconomyUser getUser(UUID uuid) {
        return economyUserMap.computeIfAbsent(uuid, k -> Economy.getInstance().getRepository().load(uuid));
    }

    public static EconomyUser createBankAccount(OfflinePlayer player) {
        Economy.getInstance().getRepository().createNewAccount(player.getUniqueId());
        return getUser(player);
    }

    public static void unloadUser(OfflinePlayer player) {
        EconomyUser user = economyUserMap.remove(player.getUniqueId());
        if (user != null) Economy.getInstance().getRepository().save(user);
    }

    public static void saveUsers() {
        for (EconomyUser user : economyUserMap.values()) {
            Economy.getInstance().getRepository().save(user);
        }
    }

    public static void modifyBalance(EconomyUser user, int amount) {
        user.deposit(amount);
        Economy.getInstance().getRepository().save(user);
    }

    public static void setBalance(EconomyUser user, int newBalance) {
        user.setBalance(newBalance);
        Economy.getInstance().getRepository().save(user);
    }

    public static TransactionResult transferMoney(EconomyUser sender, EconomyUser receiver, int amount, String comment) {

        if (sender == null) return TransactionResult.SENDER_NOT_FOUND;
        if (receiver == null) return TransactionResult.RECEIVER_NOT_FOUND;

        int senderBalance = sender.getBalance();
        BankAccountRepository bankRepo = Economy.getInstance().getRepository();
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        if (senderBalance < amount) return TransactionResult.INSUFFICIENT_FUNDS;

        sender.withdraw(amount);
        receiver.deposit(amount);
        bankRepo.save(sender);
        bankRepo.save(receiver);

        String finalComment = (comment == null || comment.isEmpty()) ? "No comment" : comment;
        String senderName = org.bukkit.Bukkit.getOfflinePlayer(sender.getPlayerUUID()).getName();
        String receiverName = org.bukkit.Bukkit.getOfflinePlayer(receiver.getPlayerUUID()).getName();
        DiscordIntegrationService.getInstance().sendToLogChannel(senderName+" transferred "+amount+" "+ConfigService.getInstance().getCurrencyAlias()+" to "+receiverName+"\nComment: "+finalComment);

        return TransactionResult.SUCCESS;
    }

}
