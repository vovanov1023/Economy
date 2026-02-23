package me.vovanov.economy.services;

import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.repositories.BankAccountRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyUserService {
    private static final Map<UUID, EconomyUser> economyUserMap = new HashMap<>();

    public enum TransactionResult {
        SUCCESS,
        INSUFFICIENT_FUNDS,
        INVALID_AMOUNT,
        SENDER_NOT_FOUND,
        RECEIVER_NOT_FOUND,
    }

    public static EconomyUser getUser(UUID playerUUID) {
        return economyUserMap.computeIfAbsent(playerUUID, uuid -> BankAccountRepo.getInstance().load(playerUUID));
    }

    public static void unloadUser(UUID playerUUID) {
        EconomyUser user = economyUserMap.remove(playerUUID);
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

    public static TransactionResult transferMoney(EconomyUser sender, EconomyUser receiver, int amount) {

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

        return TransactionResult.SUCCESS;
    }

}
