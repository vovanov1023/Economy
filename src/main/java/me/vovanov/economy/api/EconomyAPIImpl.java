package me.vovanov.economy.api;

import me.vovanov.economy.model.ATM;
import me.vovanov.economy.model.EconomyUser;
import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ATMService;
import me.vovanov.economy.services.ConfigService;
import me.vovanov.economy.services.DiscordIntegrationService;
import me.vovanov.economy.services.EconomyUserService;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class EconomyAPIImpl implements EconomyAPI {
    private final EconomyUserService userService;
    private final ATMService atmService;

    public EconomyAPIImpl(EconomyUserService userService, ATMService atmService) {
        this.userService = userService;
        this.atmService = atmService;
    }

    /**
     * Withdraws an amount of money from the user
     * @param userID the users UUID
     * @param amount the amount to withdraw
     * @return the result of the transaction
     * */
    @Override
    public TransactionResult withdrawUser(UUID userID, int amount) {
        EconomyUser user = userService.getUser(userID);
        if (user == null) return TransactionResult.USER_NOT_FOUND;
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        if (user.getBalance() < amount) return TransactionResult.INSUFFICIENT_FUNDS;
        userService.modifyBalance(user, -amount);
        return TransactionResult.SUCCESS;
    }

    /**
     * Deposits an amount of money to the user
     * @param userID the users UUID
     * @param amount the amount to deposit
     * @return the result of the transaction
     * */
    @Override
    public TransactionResult depositUser(UUID userID, int amount) {
        EconomyUser user = userService.getUser(userID);
        if (user == null) return TransactionResult.USER_NOT_FOUND;
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        userService.modifyBalance(user, amount);
        return TransactionResult.SUCCESS;
    }

    /**
     * Transfers an amount of money from user to another
     * @param senderID the sender player UUID
     * @param receiverID the receiver playerUUID
     * @param amount the amount to transfer
     * @return the result of the transaction
     * */
    @Override
    public TransactionResult transferMoney(UUID senderID, UUID receiverID, int amount) {

        EconomyUser sender = userService.getUser(senderID);
        EconomyUser receiver = userService.getUser(receiverID);

        if (sender == null) return TransactionResult.SENDER_NOT_FOUND;
        if (receiver == null) return TransactionResult.RECEIVER_NOT_FOUND;

        int senderBalance = sender.getBalance();
        if (amount <= 0) return TransactionResult.INVALID_AMOUNT;
        if (senderBalance < amount) return TransactionResult.INSUFFICIENT_FUNDS;

        userService.modifyBalance(sender, -amount);
        userService.modifyBalance(receiver, amount);

        return TransactionResult.SUCCESS;
    }

    /**
     * Gets the users balance
     * @param userID the users UUID
     * @return the balance or -1 if the user doesn't exist
     * */
    @Override
    public int getUserBalance(UUID userID) {
        EconomyUser user = userService.getUser(userID);
        if (user == null) return -1;
        return user.getBalance();
    }

    /**
     * @param userID the users UUID
    * @return true if the user has a bank account
    * */
    @Override
    public boolean hasBankAccount(UUID userID) {
        return userService.getUser(userID) != null;
    }

    @Override
    public boolean isNearATM(Player player) {
        return atmService.isNearATM(player);
    }

    @Override
    public boolean doesATMExist(int x, int y, int z, String worldName) {
        return getATMList().stream().anyMatch(atm -> atm.x() == x && atm.y() == y && atm.z() == z && atm.worldName().equals(worldName));
    }

    @Override
    public boolean doesATMExist(int id) {
        return getATMList().stream().anyMatch(atm -> atm.id() == id);
    }

    @Override
    public void createBankAccount(UUID userID) {
        userService.createBankAccount(userID);
    }

    @Override
    public ATM createATM(int x, int y, int z, String worldName) {
        return atmService.createATM(x, y, z, worldName);
    }

    @Override
    public void removeATM(int id) {
        atmService.removeATM(id);
    }

    @Override
    public List<ATM> getATMList() {
        return atmService.getATMList();
    }
}
