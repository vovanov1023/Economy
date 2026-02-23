package me.vovanov.economy.services;

import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.repositories.BankAccountRepo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyUserService {
    private static final Map<UUID, EconomyUser> economyUserMap = new HashMap<>();



    public static EconomyUser getUser(OfflinePlayer player) {
        return economyUserMap.computeIfAbsent(player.getUniqueId(), uuid -> BankAccountRepo.getInstance().load(player));
    }

    public static void unloadUser(OfflinePlayer player) {
        EconomyUser user = economyUserMap.remove(player.getUniqueId());
        if (user != null) BankAccountRepo.getInstance().save(user);
    }

    public static void modifyBalance(EconomyUser user, int amount) {
        user.deposit(amount);
        BankAccountRepo.getInstance().save(user);
    }

    public static void setBalance(EconomyUser user, int newBalance) {
        user.setBalance(newBalance);
        BankAccountRepo.getInstance().save(user);
    }

    public static TransactionResult transferMoney(EconomyUser sender, EconomyUser receiver, int amount, String comment) {

        if (sender == null) return TransactionResult.SENDER_NOT_FOUND;
        if (receiver == null) return TransactionResult.RECEIVER_NOT_FOUND;

        int senderBalance = sender.getBalance();
        BankAccountRepo bankRepo = BankAccountRepo.getInstance();
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        if (senderBalance < amount) return TransactionResult.INSUFFICIENT_FUNDS;

        sender.withdraw(amount);
        receiver.deposit(amount);
        bankRepo.save(sender);
        bankRepo.save(receiver);

        String finalComment = (comment == null || comment.isEmpty()) ? "No comment" : comment;
        DiscordIntegrationService.getInstance().sendToLogChannel(sender.getPlayer().getName()+" transferred "+amount+" "+ConfigService.getInstance().getCurrencyAlias()+" to "+receiver.getPlayer().getName()+"\nComment: "+finalComment);

        return TransactionResult.SUCCESS;
    }

}
